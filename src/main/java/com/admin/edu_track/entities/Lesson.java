package com.admin.edu_track.entities;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="lesson")
@Data
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String name; //Turkce, Matematik, Ingilizce
}
