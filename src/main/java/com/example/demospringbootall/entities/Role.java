package com.example.demospringbootall.entities;

import com.example.demospringbootall.common.ERole;
//import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;


@Entity
@Table(name = "roles")
public class Role {
    @Id
    @SequenceGenerator(name = "duong", sequenceName = "duongtv")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "duong")
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ERole name;

//    @ManyToMany(mappedBy = "roles",cascade = CascadeType.ALL)
//    @JsonIgnore
//    private Set<User> employees;
    public Role(){
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ERole getName() {
        return name;
    }

    public void setName(ERole name) {
        this.name = name;
    }
}
