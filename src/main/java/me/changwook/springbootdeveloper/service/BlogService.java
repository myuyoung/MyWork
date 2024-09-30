package me.changwook.springbootdeveloper.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import me.changwook.springbootdeveloper.domain.Article;
import me.changwook.springbootdeveloper.dto.AddArticleRequest;
import me.changwook.springbootdeveloper.dto.UpdateArticleRequest;
import me.changwook.springbootdeveloper.repository.BlogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor    //final 이 붙거나@NotNull이 붙은 필드의 생성자 추가
@Service    //빈으로 등록
public class BlogService {
    private final BlogRepository blogRepository;

    //블로그 글 추가 메서드
    //save()메서드는 JpaRepository 에서 지원하는 저장 메서드 save() AddArticleRequest 클래스에 저장된 값들을 article 데이터베이스에 저장합니다.
    public Article save(AddArticleRequest request){
        return blogRepository.save(request.toEntity());
    }

    public List<Article> findAll(){
        return blogRepository.findAll();
    }
    public Article findById(long id){
        return blogRepository.findById(id).orElseThrow(()->new IllegalArgumentException("not found" + id));
    }
    public void delete(long id){
        blogRepository.deleteById(id);
    }
    @Transactional  //트랜잭션 메서드
    public Article update(long id, UpdateArticleRequest request){
        Article article = blogRepository.findById(id).orElseThrow(()->new IllegalArgumentException("not found" + id));

        article.update(request.getTitle(), request.getContent());

        return article;
    }

}
