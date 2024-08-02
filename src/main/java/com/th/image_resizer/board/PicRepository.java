package com.th.image_resizer.board;

import com.th.image_resizer.entity.Pic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PicRepository extends JpaRepository<Pic, Long> {
}
