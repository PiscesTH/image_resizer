package com.th.image_resizer.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Pic extends BaseEntity{
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ipic;

    @Column(length = 2100)
    private String pic;

    @Column
    private int doneFl;
}
