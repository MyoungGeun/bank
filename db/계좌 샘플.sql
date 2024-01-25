insert into account_tb
		(number, password, balance, user_id, created_at)
values('1111', '1234', 1300, 1, now());        

insert into account_tb
		(number, password, balance, user_id, created_at)
values('2222', '1234', 1100, 2, now());        

insert into account_tb
		(number, password, balance, user_id, created_at)
values('3333', '1234', 0, 3, now()); 

select * from account_tb;