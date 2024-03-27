package com.vdrones.vdrones.controllers;


import com.vdrones.vdrones.dao.entity.post.PostEntity;
import com.vdrones.vdrones.dao.entity.post.ReviewsEntity;
import com.vdrones.vdrones.dao.entity.post.SubmittedApplicationsEntity;
import com.vdrones.vdrones.dao.entity.users.UserEntity;
import com.vdrones.vdrones.dao.repository.PostRepository;
import com.vdrones.vdrones.dao.repository.ReviewsRepository;
import com.vdrones.vdrones.dao.repository.SubmittedApplicationsRepository;
import com.vdrones.vdrones.dao.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;
import java.security.Principal;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

@Controller
public class Second_Controller {
    private Date setDate() {
        return new Date(new GregorianCalendar().getTimeInMillis());
    }

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ReviewsRepository reviewsRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private SubmittedApplicationsRepository submittedApplicationsRepository;

    @GetMapping("/services")
    public String services(Model model){
        Iterable<PostEntity> posts = postRepository.findAll();
        model.addAttribute("posts", posts);

        return "services";
    }

    @GetMapping("/reviews")
    public String reviews(Model model){
        Iterable<ReviewsEntity> reviews = reviewsRepository.findAll();
        model.addAttribute("reviews", reviews);

        return "reviews";
    }

    @GetMapping("/submittedApplications")
    public String submittedApplications(Model model){
        Iterable<SubmittedApplicationsEntity> submittedApplications = submittedApplicationsRepository.findAll();
        model.addAttribute("submittedApplications", submittedApplications);

        return "submittedApplications";
    }

    @GetMapping("/userApplications")
    public String userApplications(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication(); // Получаем текущую аутентификацию
        String username = authentication.getName(); // Получаем имя текущего пользователя
        UserEntity user = userService.findByUserName(username); // Получаем пользователя из сервиса
        List<SubmittedApplicationsEntity> userApplications = user.getSubmittedApplications(); // Получаем заявки текущего пользователя
        model.addAttribute("applications", userApplications); // Передаем заявки в модель для отображения на странице
        return "userApplications";
    }

    @GetMapping("/about")
    public String about(){
        return "about";
    }

    @GetMapping("/advices")
    public String advices(){
        return "advices";
    }

    @GetMapping("/add")
    public String add(){
        return "add";
    }

    @GetMapping("/addReview")
    public String addReview(){
        return "addReview";
    }

    @GetMapping("/submit")
    public String submit(){
        return "submit";
    }

    @PostMapping("/add")
    public String add(@RequestParam String title,
                           @RequestParam MultipartFile uploadFile,
                           @RequestParam String anons,
                           @RequestParam String fullText,
                           Model model){
        if(!uploadFile.isEmpty() || uploadFile.getContentType().equals("image/png") || uploadFile.getContentType().equals("image/jpg")){
            try {
                String imgFormat = "." + uploadFile.getOriginalFilename().split("\\.")[1];
                PostEntity post = new PostEntity( null, title, setDate(), anons, imgFormat, fullText);
                long postId = postRepository.save(post).getId();
                byte[] uploadFileBytes = uploadFile.getBytes();

                BufferedOutputStream bos = new BufferedOutputStream(
                        new FileOutputStream(
                                new File("C:/Users/m1m6m/IdeaProjects/vdrones/src/main/resource_image/" + title + "_" + postId + imgFormat)));

                bos.write(uploadFileBytes);
                bos.flush();
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            return "/add";
        }

        return "redirect:/services";
    }

    @PostMapping("/addReview")
    public String addReview(@RequestParam String title,
                            @RequestParam String fullText,
                            Principal principal,
                            Model model){
        if (principal != null) {
            try {
                String username = principal.getName();
                ReviewsEntity review = new ReviewsEntity(null, title, setDate(), fullText, username);
                reviewsRepository.save(review);
            } catch (Exception e) {
                e.printStackTrace();
                return "/addReview"; // Возвращаем страницу добавления отзыва в случае ошибки
            }
        } else {
            // Обработка случая, если пользователь не аутентифицирован
        }

        return "redirect:/reviews"; // Перенаправляем на страницу отзывов после успешного добавления
    }

    @PostMapping("/submit")
    public String submit(@RequestParam String title,
                         @RequestParam MultipartFile uploadFile,
                         @RequestParam String anons,
                         @RequestParam String fullText,
                         Principal principal,
                         Model model){
        if(!uploadFile.isEmpty() || uploadFile.getContentType().equals("image/png") || uploadFile.getContentType().equals("image/jpg")){
            try {
                String username = principal.getName();
                // Создаю объект заявки
                String imgFormat = "." + uploadFile.getOriginalFilename().split("\\.")[1];
                SubmittedApplicationsEntity submittedApplication = new SubmittedApplicationsEntity();
                submittedApplication.setTitle(title);
                submittedApplication.setPublicationDate(new Date(System.currentTimeMillis()));
                submittedApplication.setAnons(anons);
                submittedApplication.setImgFormat(imgFormat);
                submittedApplication.setFullText(fullText);
                submittedApplication.setStatus("Статус: Ожидает проверки");

                UserEntity user = userService.findByUserName(username);

                // Сохраняем заявку с привязкой к пользователю
                userService.saveUserWithSubmittedApplication(user, submittedApplication).getId();

                long postId = submittedApplicationsRepository.save(submittedApplication).getId();
                byte[] uploadFileBytes = uploadFile.getBytes();

                BufferedOutputStream bos = new BufferedOutputStream(
                        new FileOutputStream(
                                new File("C:/Users/m1m6m/IdeaProjects/vdrones/src/main/resource_image/" + title + "_" + postId + imgFormat)));

                bos.write(uploadFileBytes);
                bos.flush();
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            return "/submit";
        }
        return "redirect:/services";
    }

    /*@PostMapping("/submit")
    public String submit(@RequestParam String title,
                         @RequestParam MultipartFile uploadFile,
                         @RequestParam String anons,
                         @RequestParam String fullText,
                         Model model){
        if(!uploadFile.isEmpty() || uploadFile.getContentType().equals("image/png") || uploadFile.getContentType().equals("image/jpg")){
            try {
                String imgFormat = "." + uploadFile.getOriginalFilename().split("\\.")[1];
                SubmittedApplicationsEntity submittedApplication = new SubmittedApplicationsEntity(null, title, setDate(), anons, imgFormat, fullText);

                long postId = submittedApplicationsRepository.save(submittedApplication).getId();
                byte[] uploadFileBytes = uploadFile.getBytes();

                BufferedOutputStream bos = new BufferedOutputStream(
                        new FileOutputStream(
                                new File("C:/Users/tuf/IdeaProjects/vdrones/src/main/resource_image/" + title + "_" + postId + imgFormat)));

                bos.write(uploadFileBytes);
                bos.flush();
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            return "/submit";
        }
        return "redirect:/services";
    }*/

    @GetMapping("/services/{id}")
    public String post(@PathVariable(value = "id")long id, Model model){
        if(!postRepository.existsById(id)){
            return "redirect:/services";
        }

        Optional<PostEntity> postOptional = postRepository.findById(id);
        PostEntity post = postOptional.get();
        model.addAttribute("moreAboutThePost", post);

        return "more";
    }

    @GetMapping("/reviews/{id}")
    public String review(@PathVariable(value = "id")long id, Model model){
        if(!reviewsRepository.existsById(id)){
            return "redirect:/reviews";
        }
        Optional<ReviewsEntity> reviewsEntityOptional = reviewsRepository.findById(id);
        ReviewsEntity reviews = reviewsEntityOptional.get();
        model.addAttribute("moreAboutTheReview", reviews);

        return "reviewMore";
    }

    @GetMapping("/submittedApplications/{id}")
    public String submittedApplications(@PathVariable(value = "id")long id, Model model){
        if(!submittedApplicationsRepository.existsById(id)){
            return "redirect:/submittedApplications";
        }

        Optional<SubmittedApplicationsEntity> postOptional = submittedApplicationsRepository.findById(id);
        SubmittedApplicationsEntity submittedApplications = postOptional.get();
        model.addAttribute("moreAboutTheSubmittedApplication", submittedApplications);

        return "submittedApplicationsMore";
    }

    @GetMapping("/services/{id}/redact")
    public String redact(@PathVariable(value = "id") long id, Model model){
        Optional<PostEntity> postOptional = postRepository.findById(id);
        if (postOptional.isPresent()) {
            PostEntity post = postOptional.get();

            String cleanText = Jsoup.clean(post.getFullText(), Whitelist.none());
            post.setFullText(cleanText);

            model.addAttribute("moreAboutThePost", post);
            return "redact";
        } else {
            return "Error";
        }
    }

    @GetMapping("/reviews/{id}/redact")
    public String reviewRedact(@PathVariable(value = "id") long id, Model model){
        Optional<ReviewsEntity> reviewsEntityOptional = reviewsRepository.findById(id);
        if (reviewsEntityOptional.isPresent()) {
            ReviewsEntity reviews = reviewsEntityOptional.get();

            String cleanText = Jsoup.clean(reviews.getFullText(), Whitelist.none());
            reviews.setFullText(cleanText);

            model.addAttribute("moreAboutTheReview", reviews);
            return "reviewRedact";
        } else {
            return "Error";
        }
    }

    @GetMapping("/submittedApplications/{id}/submittedApplicationRedact")
    public String submittedApplicationsRedact(@PathVariable(value = "id") long id, Model model){
        Optional<SubmittedApplicationsEntity> postOptional = submittedApplicationsRepository.findById(id);
        if (postOptional.isPresent()) {
            SubmittedApplicationsEntity submittedApplication = postOptional.get();

            String cleanText = Jsoup.clean(submittedApplication.getFullText(), Whitelist.none());
            submittedApplication.setFullText(cleanText);
            
            model.addAttribute("moreAboutTheSubmittedApplication", submittedApplication);
            return "submittedApplicationRedact";
        } else {
            return "Error";
        }
    }

    @PostMapping("/services/{id}/redact")
    public String update(@RequestParam String title, @RequestParam String anons, @RequestParam String fullText, @PathVariable(value = "id") long id, Model model){
        PostEntity post = postRepository.findById(id).orElseThrow();
        post.setTitle(title);
        post.setAnons(anons);
        post.setFullText(fullText);
        post.setPublicationDate(setDate());
        postRepository.save(post);

        return "redirect:/services/{id}";
    }

    @PostMapping("/reviews/{id}/redact")
    public String reviewUpdate(@RequestParam String title, @RequestParam String fullText, Principal principal, @PathVariable(value = "id") long id, Model model){
        ReviewsEntity reviews = reviewsRepository.findById(id).orElseThrow();

        String username = principal.getName();

        reviews.setTitle(title);
        reviews.setFullText(fullText);
        reviews.setPublicationDate(setDate());
        reviewsRepository.save(reviews);

        return "redirect:/reviews/{id}";
    }

    @PostMapping("/submittedApplications/{id}/submittedApplicationRedact")
    public String submittedApplicationsUpdate(@RequestParam String anons, @RequestParam String fullText, @RequestParam String status, @PathVariable(value = "id") long id, Model model){
        SubmittedApplicationsEntity submittedApplications = submittedApplicationsRepository.findById(id).orElseThrow();
        submittedApplications.setAnons(anons);
        submittedApplications.setFullText(fullText);
        submittedApplications.setStatus(status);
        submittedApplications.setPublicationDate(setDate());
        submittedApplicationsRepository.save(submittedApplications);

        return "redirect:/submittedApplications/{id}";
    }

    @PostMapping("/services/{id}/delete")
    public String remove(@PathVariable(value = "id")long id){
        postRepository.deleteById(id);

        return "redirect:/services";
    }

    @PostMapping("/reviews/{id}/delete")
    public String reviewRemove(@PathVariable(value = "id")long id){
        reviewsRepository.deleteById(id);

        return "redirect:/reviews";
    }

    @PostMapping("/submittedApplications/{id}/submittedApplicationDelete")
    public String submittedRemove(@PathVariable(value = "id")long id){
        submittedApplicationsRepository.deleteById(id);

        return "redirect:/submittedApplications";
    }
}
