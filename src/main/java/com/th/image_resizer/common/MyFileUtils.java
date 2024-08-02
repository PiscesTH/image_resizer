package com.th.image_resizer.common;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

@Getter
@Component
public class MyFileUtils {
    private final String uploadPrefixPath;

    public MyFileUtils(@Value("${file.dir}") String uploadPrefixPath) {
        this.uploadPrefixPath = uploadPrefixPath;
    }

    //폴더 만들기
    public String makeFolders(String path) {
        File folder = new File(uploadPrefixPath, path);
        folder.mkdirs();    //make directory >> s 유/무 차이 -> 폴더 여러 개/한 개 만들기
        return folder.getAbsolutePath();    //절대 주소 : OS에서 파일을 열기 위해 들어가는 모든 경로
        //상대 주소 : 시작점(ex : D:/home/download)부터 파일을 열기 위해 들어가는 경로
        //  ../ : 상위 폴더로 이동   / : 현재 폴더에서부터
    }

    //UUID : 범용 고유 식별자
    public String getRandomFileNm() {
        return UUID.randomUUID().toString();
    }

    //확장자 얻어오기
    public String getExt(String fileNm) {
        return fileNm.substring(fileNm.lastIndexOf("."));
    }

    //랜덤 파일명 만들기 with 확장자
    public String getRandomFileNm(String originFileNm) {
        return getRandomFileNm() + getExt(originFileNm);
    }

    //랜덤 파일명 만들기 with 확장자 from MultipartFile
    public String getRandomFileNm(MultipartFile mf) {
        String fileNm = mf.getOriginalFilename();
        return getRandomFileNm(fileNm);
    }

    //메모리에 있는 내용 -> 파일로 옮기는 메서드
    public String transferTo(MultipartFile mf, String target) {
        String fileNm = getRandomFileNm(mf);
        String folderPath = makeFolders(target);
        File saveFile = new File(folderPath, fileNm);
        try {
            mf.transferTo(saveFile);    //메모리에 있는 내용 -> 실제로 파일로 옮기는 메서드. 경로는 File 객체로 보내준다.
            return fileNm;      //DB에 저장할 랜덤한 파일명 리턴.
            //DB에 저장하는 예시 -> /{category (ex)user}/(user)pk/파일명
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void delFolder(String folderPath) {   //파라미터 경로 폴더 아래의 폴더 및 파일 삭제. 풀 경로 보내줘야함
        File folder = new File(folderPath);
        if (folder.exists()) {
            File[] files = folder.listFiles();

            for (File file : files) {
                if (file.isDirectory()) {
                    delFolder(file.getAbsolutePath());
                } else {
                    file.delete();
                }
            }
            folder.delete();
        }
    }

    public void delFolderTrigger(String relativePath) {  //상대 경로 + 루트 경로 = 절대 경로 (?)
        delFolder(uploadPrefixPath + relativePath);
    }
}
