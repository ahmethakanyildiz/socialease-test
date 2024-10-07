package com.mergen.socialease.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.ArrayList;
import com.mergen.socialease.model.Review;

public interface ReviewRepository extends JpaRepository<Review, Long>  {
    Review findByreviewid(long reviewid);
    ArrayList<Review> findAllByClubid(Long clubid);
    Review findByUserName(String username);
}
