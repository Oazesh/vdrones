package com.vdrones.vdrones.dao.entity.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;


@Data
@Entity
@Table(name = "submitted_application")
@NoArgsConstructor
@AllArgsConstructor
public class SubmittedApplicationsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "title")
    private String title;
    @Column(name = "publication")
    @Temporal(TemporalType.DATE)
    private Date publicationDate;
    @Column(name = "anons")
    private String anons;
    @Column(name = "img_format")
    private String imgFormat;
    @Column(columnDefinition = "varchar(1000)")
    private String fullText;
}