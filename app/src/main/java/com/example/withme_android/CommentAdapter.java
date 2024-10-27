package com.example.withme_android;

import android.content.Context;
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

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private List<Comment> commentList;
    private Context context;
    private FirebaseAuth mAuth;
    private DatabaseReference reportRef;

    public CommentAdapter(Context context, List<Comment> commentList) {
        this.context = context;
        this.commentList = commentList;
        this.mAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
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
                        View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.report_comment_dialog, null);
                        Button yesReportComBtn, noReportComBtn;
                        ImageView closeReportComDialog;

                        yesReportComBtn = dialogView.findViewById(R.id.yesReportComBtn);
                        noReportComBtn = dialogView.findViewById(R.id.noReportComBtn);
                        closeReportComDialog = dialogView.findViewById(R.id.closeReportComDialog);

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

                        yesReportComBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String reportId = reportRef.push().getKey();
                                if(reportId !=null){
                                    Report reportCommentUser = new Report(reportId,postId,commentId,currentUserId,commentOwnerId,currentUserId);
                                    reportRef.child(reportId).setValue(reportCommentUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                holder.reportCommentIcon.setEnabled(false);
                                                Toast.makeText(view.getContext(),"Comment reported.",Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                            } else {
                                                Toast.makeText(view.getContext(), "Error reporting comment.",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                                dialog.dismiss();
                            }
                        });

                        noReportComBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
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

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView commentText, userCommentName, dateComment;
        ImageView reportCommentIcon;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            commentText = itemView.findViewById(R.id.commentText);
            userCommentName = itemView.findViewById(R.id.userCommentName);
            dateComment = itemView.findViewById(R.id.dateComment);
            reportCommentIcon = itemView.findViewById(R.id.reportCommentIcon);
        }
    }
}
