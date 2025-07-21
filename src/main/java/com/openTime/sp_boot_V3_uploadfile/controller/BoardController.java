package com.openTime.sp_boot_V3_uploadfile.controller;


import com.openTime.sp_boot_V3_uploadfile.dto.BoardDTO;
import com.openTime.sp_boot_V3_uploadfile.dto.BoardListReplyCountDTO;
import com.openTime.sp_boot_V3_uploadfile.dto.PageRequestDTO;
import com.openTime.sp_boot_V3_uploadfile.dto.PageResponseDTO;
import com.openTime.sp_boot_V3_uploadfile.service.BoardService;
import com.openTime.sp_boot_V3_uploadfile.service.ReplyService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Log4j2
@Controller
@RequestMapping("/board")
@RequiredArgsConstructor // 생성자 주입방식
public class BoardController {
    private final BoardService boardService;
    private final ReplyService replyService;

    @GetMapping("/list")
    public void list(PageRequestDTO pageRequestDTO,
                     Model model) {
        log.info("************** list *************");

        PageResponseDTO<BoardListReplyCountDTO> list = boardService.getListWithReplyCount(pageRequestDTO);
        model.addAttribute("responseDTO", list);

//        log.info("pageRequestDTO : {}", list);
    }

    @GetMapping("/add")
    public void addGet(BoardDTO boardDTO,
                       @ModelAttribute("errors") Object errors,
                       Model model) {
        log.info("************** addForm *************");
        model.addAttribute("errors", errors);  // 명시적으로 넣어주기
    }

    @PostMapping("/add")
    public String addPost(@Valid BoardDTO boardDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        log.info("************** addPost *************");

        if (bindingResult.hasErrors()) {
            log.info("ERROR : {}", bindingResult);
            redirectAttributes.addFlashAttribute("errors",bindingResult.getAllErrors());
            return "redirect:/board/add";
        }
//        log.info("boardDTO : {}", boardDTO);
        Long bno = boardService.add(boardDTO);
        log.info("[Success] New board added Bno : {}", bno);
        redirectAttributes.addFlashAttribute("bno",bno);
        return "redirect:/board/list";
    }



    @GetMapping("/read")
    public String read(Long bno, Model model, RedirectAttributes redirectAttributes, PageRequestDTO pageRequestDTO) {
        log.info("************** readGet() *************");
        BoardDTO boardDTO = boardService.searchOne(bno);
        log.info("dto : {}", boardDTO);
        if (boardDTO == null) {
            log.info("boardDTO is null");
            redirectAttributes.addFlashAttribute("errors", "The requested post does not exist. :(");
            return "redirect:/board/list";
        }
        model.addAttribute("pageRequestDTO", pageRequestDTO);
        model.addAttribute("dto", boardDTO);
        return "board/read";
    }


    @GetMapping("/modify")
    public String modify(Long bno,
                         @ModelAttribute("pageRequestDTO") PageRequestDTO pageRequestDTO,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        log.info("************* modifyGet() *************");
        BoardDTO boardDTO = boardService.searchOne(bno);
//        log.info("boardDTO : {}", boardDTO);
        if (boardDTO == null) {
            log.info("boardDTO is null");
            redirectAttributes.addFlashAttribute("errors", "The requested post does not exist. :(");
            return  "redirect:/board/list";
        }
        model.addAttribute("dto", boardDTO);
        return "board/modify";
    }

    @PostMapping("/modify")
    public String modifyPost(@Valid BoardDTO boardDTO,
                             BindingResult bindingResult,
                             @ModelAttribute("pageRequestDTO") PageRequestDTO pageRequestDTO,
                             RedirectAttributes redirectAttributes) {
        log.info("************* modifyPost() *************");
//        log.info("boardDTO : {}", boardDTO);

        if (bindingResult.hasErrors()) {
            log.info("ERROR : {}", bindingResult);
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            redirectAttributes.addAttribute("bno", boardDTO.getBno());
            return "redirect:/board/modify";
        }

        boardService.modify(boardDTO);

        redirectAttributes.addAttribute("bno", boardDTO.getBno());
        redirectAttributes.addAttribute("page", pageRequestDTO.getPage());
        redirectAttributes.addAttribute("type", pageRequestDTO.getType());
        redirectAttributes.addAttribute("keyword", pageRequestDTO.getKeyword());

        return "redirect:/board/read";
    }


    @PostMapping("/delete")
    @Transactional
    public String delete(Long bno, RedirectAttributes redirectAttributes) {
        log.info("delete bno : {}", bno);
        replyService.removeRepliesByBno(bno); // 삭제전에 해당하는 bno의 댓글 삭제

        boardService.remove(bno); // 서비스에서 삭제 메서드 호출
        redirectAttributes.addFlashAttribute("msg", "The post has been successfully deleted :)");
        return "redirect:/board/list";
    }

}
