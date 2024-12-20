package com.example.withme_android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private List<Post> postList;
    private Context context;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseReference postreference = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getUid()).child("posts");
    private AlertDialog dialog;

    public PostAdapter(Context context,List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_post_item, parent, false);
        return new PostAdapter.PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        DatabaseReference reportRef = FirebaseDatabase.getInstance().getReference("reportedPosts");

        holder.postOwnerName.setText(post.getName());
        holder.postLocation.setText(post.getLocation());
        holder.postDate.setText(post.getPostDate());
        holder.yummysNumber.setText(String.valueOf(post.getYummysNumber()));
        holder.commentsNumber.setText(String.valueOf(post.getCommentsNumber()));

        Glide.with(holder.userAvatar.getContext())
                .load(post.getUserPhotoUrl())
                .apply(new RequestOptions().placeholder(R.drawable.small_logo).error(R.drawable.baseline_error_outline_24))
                .into(holder.userAvatar);

        Glide.with(holder.postPicture.getContext())
                .load(post.getPostImageUrl())
                .apply(new RequestOptions().placeholder(R.drawable.small_logo).error(R.drawable.baseline_error_outline_24))
                .into(holder.postPicture);

        holder.postPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,User_PostView.class);
                intent.putExtra("postId",post.getPostId());
                intent.putExtra("userId",post.getUserId());
                context.startActivity(intent);
            }
        });

        holder.postMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    Post editPostPosition = postList.get(currentPosition);
                    String postId = post.getPostId();
                    String ownerId = post.getUserId();
                    String currentUserId = mAuth.getUid();

                    if(currentUserId.equals(ownerId)){
                        View editView = LayoutInflater.from(view.getContext()).inflate(R.layout.editpost_dialog, null);
                        dialog = new AlertDialog.Builder(view.getContext()).setView(editView).create();

                        ImageView closeEditPostDialog = editView.findViewById(R.id.closeEditPostDialog);
                        ImageView deletePost = editView.findViewById(R.id.deletePost);
                        ImageView editPost = editView.findViewById(R.id.editPost);

                        closeEditPostDialog.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });

                        editPost.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(context,User_EditPost.class);
                                intent.putExtra("postId", postId);
                                context.startActivity(intent);
                                dialog.dismiss();
                            }
                        });

                        deletePost.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (currentPosition != RecyclerView.NO_POSITION && currentPosition < postList.size()) {
                                    postList.remove(currentPosition);
                                    notifyItemRemoved(currentPosition);
                                }
                                postreference.child(mAuth.getUid()).child(postId).removeValue();
                                dialog.dismiss();
                            }
                        });
                        if (context instanceof Activity && !((Activity) context).isFinishing() && !((Activity) context).isDestroyed()) {
                            dialog.show();
                        }
                    } else {
                        View reportView = LayoutInflater.from(view.getContext()).inflate(R.layout.reportpost_dialog, null);
                        AlertDialog dialog = new AlertDialog.Builder(view.getContext()).setView(reportView).create();
                        ImageView closeReportPostDialog;
                        Button yesReportPostBtn, noReportPostBtn;

                        closeReportPostDialog = reportView.findViewById(R.id.closeReportPostDialog);
                        yesReportPostBtn = reportView.findViewById(R.id.yesReportPostBtn);
                        noReportPostBtn = reportView.findViewById(R.id.noReportPostBtn);

                        closeReportPostDialog.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });

                        yesReportPostBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String reportId = reportRef.push().getKey();
                                if(reportId !=null){
                                    Report reportCommentUser = new Report(reportId,postId,ownerId,currentUserId);
                                    reportRef.child(reportId).setValue(reportCommentUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                holder.postMenu.setEnabled(false);
                                                Toast.makeText(view.getContext(),"Post reported.",Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                            } else {
                                                Toast.makeText(view.getContext(), "Error reporting post.",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                                dialog.dismiss();
                            }
                        });

                        noReportPostBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        if (context instanceof Activity && !((Activity) context).isFinishing() && !((Activity) context).isDestroyed()) {
                            dialog.show();
                        }
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (postList != null) {
            return postList.size();
        } else {
            return 0;
        }
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView postOwnerName, postLocation, yummysNumber, commentsNumber, postDate;
        ImageView userAvatar, postPicture;
        LinearLayout postMenu;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            postOwnerName = itemView.findViewById(R.id.postOwnerName);
            postLocation = itemView.findViewById(R.id.postLocation);
            yummysNumber = itemView.findViewById(R.id.yummysNumber);
            commentsNumber = itemView.findViewById(R.id.commentsNumber);
            postDate = itemView.findViewById(R.id.postDate);
            userAvatar = itemView.findViewById(R.id.userAvatar);
            postPicture = itemView.findViewById(R.id.postPicture);
            postMenu = itemView.findViewById(R.id.postMenu);
        }
    }
}
