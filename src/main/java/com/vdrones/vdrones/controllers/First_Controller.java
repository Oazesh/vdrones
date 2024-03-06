package com.vdrones.vdrones.controllers;

import com.vdrones.vdrones.dao.entity.post.PostEntity;
import com.vdrones.vdrones.dao.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.Iterator;


@Controller
public class First_Controller {
    @Autowired
    private PostRepository postRepository;

    @GetMapping("/")
    public String mainPackage(Model model){
        Iterable<PostEntity> optional = postRepository.findAll();
        ArrayList<PostEntity> posts = new ArrayList<>(3);
        Iterator<PostEntity> iterator = optional.iterator();
        int counter = 0;
        while (iterator.hasNext()){
            if(counter > 3){
                break;
            }

            posts.add(iterator.next());
            counter++;
        }

        model.addAttribute("threePostForGeneral", posts);

        return "general";
    }
}
