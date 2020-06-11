package controllers;

import model.Category;
import model.Comment;
import model.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import service.category.CategoryService;
import service.comment.CommentService;
import service.post.PostService;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.*;

@Controller
@RequestMapping("/blog")
public class PostController {
    private final PostService postService;

    private final CategoryService categoryService;

    private final CommentService commentService;

    @Autowired
    public PostController(PostService postService, CategoryService categoryService, CommentService commentService) {
        this.postService = postService;
        this.categoryService = categoryService;
        this.commentService = commentService;
    }

    @ModelAttribute(name = "categoryList")
    public List<Category> tagList() {
        return categoryService.findAll();
    }

    @ModelAttribute(name = "datePostMap")
    public Map<Year, Set<Month>> datePostMap() {
        Map<Year, Set<Month>> dateMap = new HashMap<>();
        List<Post> postList = postService.findAll(Sort.by("createTime").ascending());
        for (Post post : postList) {
            LocalDate postDate = post.getCreateTime().toLocalDateTime().toLocalDate();
            Year postYear = Year.of(postDate.getYear());
            Month postMonth = postDate.getMonth();
            if (dateMap.get(postYear) == null) {
                dateMap.put(postYear, new LinkedHashSet<>());
            }
            dateMap.get(postYear).add(postMonth);
        }
        return dateMap;
    }

    @GetMapping("/{id}")
    public ModelAndView modelAndView(@PathVariable("id") Long id) {
        ModelAndView modelAndView = new ModelAndView("public/detail");
        Post postSelected = postService.findOne(id);
        List<Comment> comments = commentService.findCommentsByPost(postSelected, Sort.by("createTime").descending());
        if (comments == null) {
            comments = new LinkedList<>();
        }
        modelAndView.addObject("postSelected", postSelected);
        modelAndView.addObject("comments", comments);
        modelAndView.addObject("comment", new Comment());
        return modelAndView;
    }

    @PostMapping("/submit/")
    public RedirectView submitComment(@ModelAttribute("comment") Comment comment) {
        comment.setCreateTime(new Timestamp(System.currentTimeMillis()));
        Post post = comment.getPost();
        commentService.save(comment);
        return new RedirectView("/blog/" + post.getId());
    }
}
