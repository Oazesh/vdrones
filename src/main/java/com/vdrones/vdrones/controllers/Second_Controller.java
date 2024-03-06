package com.vdrones.vdrones.controllers;


import com.vdrones.vdrones.dao.entity.post.PostEntity;
import com.vdrones.vdrones.dao.entity.post.SubmittedApplicationsEntity;
import com.vdrones.vdrones.dao.repository.PostRepository;
import com.vdrones.vdrones.dao.repository.SubmittedApplicationsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.util.GregorianCalendar;
import java.util.Optional;

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
    private SubmittedApplicationsRepository submittedApplicationsRepository;

    @GetMapping("/services")
    public String services(Model model){
        Iterable<PostEntity> posts = postRepository.findAll();
        model.addAttribute("posts", posts);

        return "services";
    }

    @GetMapping("/submittedApplications")
    public String submittedApplications(Model model){
        Iterable<SubmittedApplicationsEntity> submittedApplications = submittedApplicationsRepository.findAll();
        model.addAttribute("submittedApplications", submittedApplications);

        return "submittedApplications";
    }

    @GetMapping("/add")
    public String add(){
        return "add";
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
                                new File("C:/Users/tuf/IdeaProjects/vdrones/src/main/resource_image/" + title + "_" + postId + imgFormat)));

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

    @PostMapping("/submit")
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
    }

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

    @PostMapping("/submittedApplications/{id}/submittedApplicationRedact")
    public String submittedApplicationsUpdate(@RequestParam String title, @RequestParam String anons, @RequestParam String fullText, @PathVariable(value = "id") long id, Model model){
        SubmittedApplicationsEntity submittedApplications = submittedApplicationsRepository.findById(id).orElseThrow();
        submittedApplications.setTitle(title);
        submittedApplications.setAnons(anons);
        submittedApplications.setFullText(fullText);
        submittedApplications.setPublicationDate(setDate());
        submittedApplicationsRepository.save(submittedApplications);

        return "redirect:/submittedApplications/{id}";
    }

    @PostMapping("/services/{id}/delete")
    public String remove(@PathVariable(value = "id")long id){
        postRepository.deleteById(id);

        return "redirect:/services";
    }

    @PostMapping("/submittedApplications/{id}/submittedApplicationDelete")
    public String submittedRemove(@PathVariable(value = "id")long id){
        submittedApplicationsRepository.deleteById(id);

        return "redirect:/submittedApplications";
    }
}
