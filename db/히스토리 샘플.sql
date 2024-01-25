-- 이체 내역 기록
-- 1번 계좌에서 2번 계좌로 100원 이체한다.

insert into history_tb(amount, w_balance, d_balance,
			w_account_id, d_account_id, created_at)
values(100, 1200, 1200, 1, 2, now());

-- AMT 출금만, 1번 계좌에서 100원만 출금하는 내역을 만들어보자

insert into history_tb(amount, w_balance, d_balance,
			w_account_id, d_account_id, created_at)
values(100, 1100, null, 1, null, now());

-- 입금 내역만, 1번 계좌에서 500원 입금 내역을 만들어보자.

insert into history_tb(amount, w_balance, d_balance,
			w_account_id, d_account_id, created_at)
values(500, null, 1600, null, 1, now());

select * from history_tb;

