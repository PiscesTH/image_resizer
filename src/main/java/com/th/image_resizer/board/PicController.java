package com.th.image_resizer.board;

import com.th.image_resizer.board.model.PicsDto;
import com.th.image_resizer.response.ApiResponse;
import com.th.image_resizer.response.ResVo;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pic")
public class PicController {
    private final PicService service;

    @PostMapping
    public ApiResponse<?> postBoard(@RequestPart List<MultipartFile> pics,
                                    @RequestPart(required = false) PicsDto dto) {
        log.info("pics : {}", pics);
        log.info("dto : {}", dto);
        return service.postPic(pics, dto);
    }

    @GetMapping
    public ResponseEntity<Resource> downloadImage(@RequestParam long ipic) {
        log.info("ipic : {}", ipic);
        return service.downloadImage(ipic);
    }
}
