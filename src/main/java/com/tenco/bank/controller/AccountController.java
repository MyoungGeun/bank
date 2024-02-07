package com.tenco.bank.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.tenco.bank.dto.AccountSaveFormDto;
import com.tenco.bank.dto.DepositFormDto;
import com.tenco.bank.dto.TransferFormDto;
import com.tenco.bank.dto.WithdrawFormDto;
import com.tenco.bank.handler.exception.CustomRestfulException;
import com.tenco.bank.repository.entity.Account;
import com.tenco.bank.repository.entity.CustomHistoryEntity;
import com.tenco.bank.repository.entity.User;
import com.tenco.bank.service.AccountService;
import com.tenco.bank.utils.Define;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/account")
public class AccountController {

	@Autowired
	private HttpSession session;
	@Autowired
	private AccountService accountService;

	/**
	 * 계좌 생성 페이지 요청
	 * 
	 * @return saveForm.jsp
	 */
	@GetMapping("/save")
	public String savePage() {
		User principal = (User) session.getAttribute(Define.PRINCIPAL);

		return "account/saveForm";
	}

	/**
	 * 계좌 생성 로직
	 * 
	 * @param dto
	 * @return list.jsp
	 */
	@PostMapping("/save")
	public String saveProc(AccountSaveFormDto dto) {

		User principal = (User) session.getAttribute(Define.PRINCIPAL);

		if (dto.getNumber() == null || dto.getNumber().isEmpty()) {
			throw new CustomRestfulException("계좌번호를입력하세요", HttpStatus.BAD_REQUEST);
		}
		if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
			throw new CustomRestfulException("계좌비밀번호를입력하세요", HttpStatus.BAD_REQUEST);
		}
		if (dto.getBalance() == null || dto.getBalance() < 0) {
			throw new CustomRestfulException("잘못된 금액 입니다", HttpStatus.BAD_REQUEST);
		}

		accountService.createAccount(dto, principal.getId());

		return "redirect:/account/list";
	}

	/**
	 * 계좌 목록 페이지 요청
	 * 
	 * @param model - accountList
	 * @return list.jsp
	 */
	@GetMapping({ "/list", "/" })
	public String listPage(Model model) {

		User principal = (User) session.getAttribute(Define.PRINCIPAL);

		List<Account> accountList = accountService.readAccountListByUserId(principal.getId());

		if (accountList.isEmpty()) {
			model.addAttribute("accountList", null);
		} else {
			model.addAttribute("accountList", accountList);
		}

		return "account/list";
	}

	/**
	 * 출금 페이지 요청
	 * 
	 * @return withdraw.jsp
	 */
	@GetMapping("/withdraw")
	public String withdrawPage() {
		return "account/withdraw";
	}

	/**
	 * 출금 요청 로직
	 * 
	 * @param dto
	 * @return list.jsp
	 */
	@PostMapping("/withdraw")
	public String withdrawProc(WithdrawFormDto dto) {

		User principal = (User) session.getAttribute(Define.PRINCIPAL);

		if (dto.getAmount() == null) {
			throw new CustomRestfulException("금액을 입력 하시오", HttpStatus.BAD_REQUEST);
		}

		if (dto.getAmount().longValue() <= 0) {
			throw new CustomRestfulException("출금 금액이 0원 이하일 수 없습니다", HttpStatus.BAD_REQUEST);
		}

		if (dto.getWAccountNumber() == null || dto.getWAccountNumber().isEmpty()) {
			throw new CustomRestfulException("계좌 번호를 입력 하시오", HttpStatus.BAD_REQUEST);
		}

		if (dto.getWAccountPassword() == null || dto.getWAccountPassword().isEmpty()) {
			throw new CustomRestfulException("계좌 비밀 번호를 입력 하시오", HttpStatus.BAD_REQUEST);
		}

		accountService.updateAccountWithdraw(dto, principal.getId());

		return "redirect:/account/list";
	}

	/**
	 * 입금 페이지 요청
	 * 
	 * @return deposit.jsp
	 */
	@GetMapping("/deposit")
	public String depositPage() {
		return "account/deposit";
	}

	/**
	 * 입급 요청 로직
	 * 
	 * @param dto
	 * @return list.jsp
	 */
	@PostMapping("/deposit")
	public String depositProc(DepositFormDto dto) {

		User principal = (User) session.getAttribute(Define.PRINCIPAL);

		if (dto.getAmount() == null) {
			throw new CustomRestfulException(Define.ENTER_YOUR_BALANCE, HttpStatus.BAD_REQUEST);
		}
		if (dto.getAmount().longValue() <= 0) {
			throw new CustomRestfulException(Define.D_BALANCE_VALUE, HttpStatus.BAD_REQUEST);
		}
		if (dto.getDAccountNumber() == null || dto.getDAccountNumber().isEmpty()) {
			throw new CustomRestfulException(Define.ENTER_YOUR_ACCOUNT_NUMBER, HttpStatus.BAD_REQUEST);
		}

		accountService.updateAccountDeposit(dto, principal.getId());

		return "redirect:/account/list";

	}

	/**
	 * 이체 페이지 로직
	 * 
	 * @return transfer.jsp
	 */
	@GetMapping("/transfer")
	public String transferPage() {
		return "account/transfer";
	}

	/**
	 * 이체 요청 로직
	 * 
	 * @param dto
	 * @return list.jsp
	 */
	@PostMapping("/transfer")
	public String transferProc(TransferFormDto dto) {

		User principal = (User) session.getAttribute(Define.PRINCIPAL);

		if (dto.getAmount() == null) {
			throw new CustomRestfulException(Define.ENTER_YOUR_BALANCE, HttpStatus.BAD_REQUEST);
		}
		if (dto.getAmount().longValue() <= 0) {
			throw new CustomRestfulException(Define.D_BALANCE_VALUE, HttpStatus.BAD_REQUEST);
		}
		if (dto.getWAccountNumber() == null || dto.getWAccountNumber().isEmpty()) {
			throw new CustomRestfulException("출금하실 계좌번호를 입력해주세요.", HttpStatus.BAD_REQUEST);
		}
		if (dto.getDAccountNumber() == null || dto.getDAccountNumber().isEmpty()) {
			throw new CustomRestfulException("이체하실 계좌번호를 입력해주세요.", HttpStatus.BAD_REQUEST);
		}
		if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
			throw new CustomRestfulException(Define.ENTER_YOUR_PASSWORD, HttpStatus.BAD_REQUEST);
		}

		accountService.updateAccountTransfer(dto, principal.getId());

		return "redirect:/account/list";
	}

	/**
	 * 계좌 상세 보기 페이지 요청 -- 전체(입출금), 입금, 출금
	 * 
	 * @param id
	 * @param type
	 * @param model
	 * @return detail.jsp
	 */
	@GetMapping("/detail/{id}")
	public String detail(@PathVariable Integer id,
			@RequestParam(name = "type", defaultValue = "all", required = false) String type, Model model) {

		Account account = accountService.readByAccountId(id);

		List<CustomHistoryEntity> historyList = accountService.readHistoryListByAccount(type, id);
		System.out.println("list : " + historyList.toString());

		model.addAttribute("account", account);
		model.addAttribute("historyList", historyList);

		return "account/detail";
	}

}