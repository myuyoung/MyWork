package me.changwook.springbootdeveloper.repository;

import me.changwook.springbootdeveloper.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BlogRepository extends JpaRepository<Article,Long> {

}
