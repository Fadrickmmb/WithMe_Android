package com.example.withme_android;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class Admin_CommentAdapter extends RecyclerView.Adapter<Admin_CommentAdapter.Admin_CommentViewHolder> {
    private List<Comment> commentList;
    private Context context;
    private FirebaseAuth mAuth;
    private DatabaseReference commentReference;

    public Admin_CommentAdapter(Context context, List<Comment> commentList) {
        this.context = context;
        this.commentList = commentList;
        this.mAuth = FirebaseAuth.getInstance();

    }

    @NonNull
    @Override
    public Admin_CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        return new Admin_CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Admin_CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        String currentUserId;
        DatabaseReference reportRef = FirebaseDatabase.getInstance().getReference("reportedComments");

        holder.commentText.setText(comment.getText());
        holder.userCommentName.setText(comment.getName());
        holder.dateComment.setText(comment.getDate());

        if(mAuth != null){
            currentUserId = mAuth.getUid();
        } else {
            currentUserId = null;
        }

        reportRef.orderByChild("commentId").equalTo(comment.getCommentId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot reportSnapshot : snapshot.getChildren()) {
                    String userReportingId = reportSnapshot.child("userReportingId").getValue(String.class);
                    if (userReportingId != null && userReportingId.equals(currentUserId)) {
                        holder.reportCommentIcon.setEnabled(false);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.reportCommentIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    Comment reportComment = commentList.get(currentPosition);
                    String postId = reportComment.getPostId();
                    String commentId = reportComment.getCommentId();
                    String commentOwnerId = reportComment.getUserId();

                    if(!currentUserId.equals(commentOwnerId)){
                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                        View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.admineditpost_dialog, null);
                        ImageView closeReportComDialog, deletePost;

                        closeReportComDialog = dialogView.findViewById(R.id.closeDeletePostDialog);
                        deletePost = dialogView.findViewById(R.id.deletePost);

                        builder.setView(dialogView);
                        AlertDialog dialog = builder.create();
                        dialog.setCancelable(true);
                        dialog.show();

                        closeReportComDialog.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });

                        deletePost.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int currentPosition = holder.getAdapterPosition();
                                if (currentPosition != RecyclerView.NO_POSITION && currentPosition < commentList.size()) {
                                    Comment reportComment = commentList.get(currentPosition);
                                    String postId = reportComment.getPostId();
                                    String commentId = reportComment.getCommentId();
                                    String commentOwnerId = reportComment.getUserId();

                                    commentList.remove(currentPosition);
                                    notifyItemRemoved(currentPosition);
                                    commentReference = FirebaseDatabase.getInstance().getReference("users")
                                            .child(commentOwnerId).child("posts").child(postId).child("comments").child(commentId);
                                    commentReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(context, "Comment deleted successfully.", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(context, "Failed to delete comment.", Toast.LENGTH_SHORT).show();
                                                        Log.e("Admin_CommentAdapter", "Firebase deletion failed: " + task.getException().getMessage());
                                                    }
                                                }
                                            });
                                    dialog.dismiss();
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class Admin_CommentViewHolder extends RecyclerView.ViewHolder {
        TextView commentText, userCommentName, dateComment;
        ImageView reportCommentIcon;

        public Admin_CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            commentText = itemView.findViewById(R.id.commentText);
            userCommentName = itemView.findViewById(R.id.userCommentName);
            dateComment = itemView.findViewById(R.id.dateComment);
            reportCommentIcon = itemView.findViewById(R.id.reportCommentIcon);
        }
    }
}
