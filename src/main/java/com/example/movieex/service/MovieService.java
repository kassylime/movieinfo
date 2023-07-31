package com.example.movieex.service;

import com.example.movieex.entity.Movie;
import com.example.movieex.repository.MovieReprository;
import com.example.movieex.util.PagingUtil;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.xml.transform.sax.SAXResult;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class MovieService {
    @Autowired
    private MovieReprository mRepo;

    private ModelAndView mv;

    public ModelAndView getMovieList(Integer pageNum, HttpSession session) {
        log.info("getMovieList()");

        if (pageNum == null) {
            pageNum = 1;
        }
        int listCnt = 3;

        Pageable pb = PageRequest.of((pageNum - 1), listCnt, Sort.Direction.DESC, "mcode");

        Page<Movie> result = mRepo.findByMcodeGreaterThan(0L, pb);

        List<Movie> mList = result.getContent();

        // 페이지처리
        int totalPage = result.getTotalPages();

        String paging = getPaging(pageNum, totalPage);

        session.setAttribute("pageNum", pageNum);

        mv = new ModelAndView();
        mv.addObject("mList", mList);

        mv.addObject("paging", paging);

        mv.setViewName("home");
        return mv;
    }

    private String getPaging(Integer pageNum, int totalPage) {
        log.info("getPaging()");
        String pageHtml = null;
        int pageCnt = 3;
        String urlstr = "?";

        PagingUtil paging = new PagingUtil(totalPage, pageNum, pageCnt, urlstr);

        pageHtml = paging.makePaging();
        return pageHtml;
    }

    public String insertMovie(List<MultipartFile> files, Movie movie, HttpSession session, RedirectAttributes rttr) {
        log.info("insertMovie()");
        String view = null;
        String msg = null;

        String upFile = files.get(0).getOriginalFilename();

        try {
            if (!upFile.equals("")) {
                fileUpload(files, session, movie);
            }
            mRepo.save(movie);
            view = "redirect:/";
            msg = "등록 성공";
        } catch (Exception e) {
            e.printStackTrace();
            view = "redirect:writeForm";
            msg = "등록 실패";
        }
        rttr.addFlashAttribute("msg", msg);
        return view;
    }

    private void fileUpload(List<MultipartFile> files, HttpSession session, Movie movie) throws IOException {
        log.info("fileUpload()");
        String sysname = null;
        String oriname = null;

        String realPath = session.getServletContext().getRealPath("/");
        realPath += "upload/";
        File folder = new File(realPath);
        if (folder.isDirectory() == false) {
            folder.mkdir();
        }
        MultipartFile mf = files.get(0);
        oriname = mf.getOriginalFilename();

        sysname = System.currentTimeMillis() + oriname.substring(oriname.lastIndexOf("."));
        File file = new File(realPath + sysname);
        mf.transferTo(file);

        movie.setMoriname(oriname);
        movie.setMsysname(sysname);
    }

    public ModelAndView getMovie(Long mcode) {
        log.info("getMovie()");
        mv = new ModelAndView();
        Movie movie = mRepo.findById(mcode).get();
        mv.addObject("movie", movie);
        return mv;
    }

    public String updateMovie(List<MultipartFile> files, Movie movie, HttpSession session, RedirectAttributes rttr) {
        log.info("updateMovie()");
        String view = null;
        String msg = null;
        String upFile = files.get(0).getOriginalFilename();
        String poster = movie.getMsysname();

        try {
            if (!upFile.equals("")) {
                fileUpload(files, session, movie);
            }
            mRepo.save(movie);
            if (poster != null && !upFile.equals("")) {
                fileDelete(poster, session);
            }

            view = "redirect:detail?mcode=" + movie.getMcode();
            msg = "수정 성공";
        } catch (Exception e) {
            e.printStackTrace();
            view = "redirect:updateForm?mcode=" + movie.getMcode();
            msg = "수정 실패";
        }
        rttr.addFlashAttribute("msg", msg);
        return view;
    }

    private void fileDelete(String poster, HttpSession session) throws Exception {
        log.info("fileDelete()");
        String realPath = session.getServletContext().getRealPath("/");
        realPath += "upload/" + poster;
        File file = new File(realPath);

        if (file.exists()) {
            file.delete();
        }
    }

    public String deleteMovie(Long mcode, String msysname, HttpSession session, RedirectAttributes rttr) {
        log.info("deleteMovie()");
        String view = null;
        String msg = null;

        try {
            if(msysname != null){
                fileDelete(msysname, session);
            }
            mRepo.deleteById(mcode);
            view = "redirect:/";
            msg = "삭제 성공";
        } catch (Exception e){
            e.printStackTrace();
            view = "redirect:detail?mcode=" + mcode;
            msg = "삭제 실패";
        }
        rttr.addFlashAttribute("msg", msg);
        return view;
    }
}
