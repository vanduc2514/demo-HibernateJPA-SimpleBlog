package controllers;

import model.Comment;
import model.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;
import service.comment.CommentService;

import java.sql.Timestamp;

@Controller
@RequestMapping("/comment")
public class CommentController {
    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/submit")
    public RedirectView submitComment(@ModelAttribute("comment") Comment comment) {
        comment.setCreateTime(new Timestamp(System.currentTimeMillis()));
        Post post = comment.getPost();
        commentService.save(comment);
        return new RedirectView("/blog/" + post.getId());
    }
}