package com.vdrones.vdrones.dao.entity.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "reviews_entity")
@NoArgsConstructor
@AllArgsConstructor
public class ReviewsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "title")
    private String title;
    @Column(name = "publication")
    @Temporal(TemporalType.DATE)
    private Date publicationDate;
    @Column(name = "fullText")
    private String fullText;
    @Column(name = "username")
    private String username;
}
