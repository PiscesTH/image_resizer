package com.th.image_resizer.board;

import com.th.image_resizer.board.model.PicsDto;
import com.th.image_resizer.response.ApiResponse;
import com.th.image_resizer.response.ResVo;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pic")
public class PicController {
    private final PicService service;

    @PostMapping
    public ApiResponse<ResVo> postBoard(@RequestPart List<MultipartFile> pics,
                                        @RequestPart(required = false) PicsDto dto) {
        log.info("pics : {}", pics);
        log.info("dto : {}", dto);
        return service.postPic(pics, dto);
    }
}
