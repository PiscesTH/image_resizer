package com.th.image_resizer.board;

import com.th.image_resizer.board.model.PicsDownloadDto;
import com.th.image_resizer.board.model.PicsDto;
import com.th.image_resizer.common.MyFileUtils;
import com.th.image_resizer.entity.Pic;
import com.th.image_resizer.exception.RestApiException;
import com.th.image_resizer.response.ApiResponse;
import com.th.image_resizer.response.ResVo;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static com.th.image_resizer.exception.CommonErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PicService {
    private final PicRepository picRepository;
    private final MyFileUtils fileUtils;
    @Value("${file.dir}")
    private String downloadPath;
    private String savePath = "C:/download/";

    @Transactional
    public ApiResponse<ResVo> postPic(List<MultipartFile> pics, PicsDto dto) {
//        for (MultipartFile pic : pics) {
//            log.info("{}", pic.getContentType());
//            if (!pic.getContentType().startsWith("image")){
//                return new ApiResponse<>(new ResVo());
//            }
//        }
//        log.info("2 -----------------");

        Pic resultPic = null;
        for (MultipartFile pic : pics) {
            Pic tmpPic = new Pic();
            tmpPic.setDoneFl(0);
            resultPic = picRepository.save(tmpPic);
            String target = "/pic/" + tmpPic.getIpic();
            String saveFileNm = fileUtils.transferTo(pic, target);
            resultPic.setPic("s_" + saveFileNm);
//            picRepository.save(resultPic);

            //썸네일 파일 경로 설정
            String savedFilePath = downloadPath + target + "/" + saveFileNm;
            String saveThumbnailPath = downloadPath + target + "/s_" + saveFileNm;
            log.info(savedFilePath);
            log.info(saveThumbnailPath);

            //썸네일 작업(크기 고정)
            try {
                if (dto != null && dto.getWidth() + dto.getHeight() != 0) {
                    Thumbnailator.createThumbnail(new File(savedFilePath), new File(saveThumbnailPath), dto.getWidth(), dto.getHeight());
                } else {
                    //썸네일(비율)
                    File savedFile = new File(savedFilePath);
                    BufferedImage bo_image = ImageIO.read(savedFile);
                    double ratio = 2;
                    int width = (int) (bo_image.getWidth() / ratio);
                    int height = (int) (bo_image.getHeight() / ratio);
                    Thumbnailator.createThumbnail(savedFile, new File(saveThumbnailPath), width, height);
                }
                File file = new File(savedFilePath);
                file.delete();
            } catch (Exception e) {
                throw new RestApiException(INTERNAL_SERVER_ERROR);
            }
        }
        return new ApiResponse<>("200", "작업이 완료되었습니다.", new ResVo((resultPic.getIpic().intValue())));
    }

    public void downloadImage(PicsDownloadDto dto, HttpServletResponse response) {
        String target = "/pic/" + dto.getIpic();
        String savedFileNm = picRepository.getReferenceById(dto.getIpic()).getPic();
        String savedFilePath = downloadPath + target + "/" + savedFileNm;
        String saveThumbnailPath = downloadPath + target + "/s_" + savedFileNm;
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        File downloadFile = new File(saveThumbnailPath);
        int fSize = (int) downloadFile.length();
        try {
            // 파일명을 URLEncoder 하여 attachment, Content-Disposition Header로 설정
            String encodedFilename = "attachment; filename*=" + "UTF-8" + "''" + URLEncoder.encode("/s_" + savedFileNm, "UTF-8");

            // ContentType 설정
            response.setContentType("application/octet-stream; charset=utf-8");

            // Header 설정
            response.setHeader("Content-Disposition", encodedFilename);

            // ContentLength 설정
            response.setContentLengthLong(fSize);



                /* BufferedInputStream
                 *
                java.io의 가장 기본 파일 입출력 클래스
                입력 스트림(통로)을 생성해줌
                사용법은 간단하지만, 버퍼를 사용하지 않기 때문에 느림
                속도 문제를 해결하기 위해 버퍼를 사용하는 다른 클래스와 같이 쓰는 경우가 많음
                */
            in = new BufferedInputStream(new FileInputStream(downloadFile));

                /* BufferedOutputStream
                 *
                java.io의 가장 기본이 되는 파일 입출력 클래스
                출력 스트림(통로)을 생성해줌
                사용법은 간단하지만, 버퍼를 사용하지 않기 때문에 느림
                속도 문제를 해결하기 위해 버퍼를 사용하는 다른 클래스와 같이 쓰는 경우가 많음
                */
            out = new BufferedOutputStream(response.getOutputStream());


            try {
                byte[] buffer = new byte[4096];
                int bytesRead = 0;

                    /*
                    모두 현재 파일 포인터 위치를 기준으로 함 (파일 포인터 앞의 내용은 없는 것처럼 작동)
                    int read() : 1byte씩 내용을 읽어 정수로 반환
                    int read(byte[] b) : 파일 내용을 한번에 모두 읽어서 배열에 저장
                    int read(byte[] b. int off, int len) : 'len'길이만큼만 읽어서 배열의 'off'번째 위치부터 저장
                    */
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }

                // 버퍼에 남은 내용이 있다면, 모두 파일에 출력
                out.flush();
            } finally {
                    /*
                    현재 열려 in,out 스트림을 닫음
                    메모리 누수를 방지하고 다른 곳에서 리소스 사용이 가능하게 만듬
                    */
                in.close();
                out.close();
                fileUtils.delFolder(downloadPath + target);
            }
        } catch (Exception e) {
            throw new RestApiException(INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Resource> downloadImage(long ipic) {
        String target = "/pic/" + ipic;
        Optional<Pic> optPic = picRepository.findById(ipic);
        Pic pic = optPic.get();
        log.info(pic.getPic());
        String absFilePath = downloadPath + target + "/" + pic.getPic();
        try {
            log.info(absFilePath);
//            Path filePath = Paths.get(downloadPath+"pic/"+ipic).resolve(pic.getPic()).normalize();
            Path filePath = Paths.get(absFilePath).toAbsolutePath().normalize();
            Resource resource = new FileSystemResource(filePath);

            if (resource.exists() && resource.isFile()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            }

//            String contentType = Files.probeContentType(filePath);
//            if (contentType == null) {
//                contentType = "application/octet-stream";
//            }
            throw new RestApiException(INTERNAL_SERVER_ERROR);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } finally {
//            fileUtils.delFolder(downloadPath + target);
//            picRepository.delete(pic);
        }
    }
}
