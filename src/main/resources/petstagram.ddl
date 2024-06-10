/* 쿼리문 실험 */
SELECT * FROM postLikes WHERE post_id = 20;

DELETE FROM notifications WHERE post_id = 20;
DELETE FROM images WHERE post_id = 20;
DELETE FROM postLikes WHERE post_id = 20;
DELETE FROM replyCommentLikes WHERE reply_comment_id = 5;
DELETE FROM reply_comments WHERE comment_id= 60;
DELETE FROM commentLikes WHERE comment_id = 52;
DELETE FROM comments WHERE post_id = 20;
DELETE FROM posts WHERE post_id = 20;