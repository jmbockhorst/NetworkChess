drop database if exists network_chess;
create database network_chess;
use network_chess;

CREATE USER `chess_user`@`localhost` IDENTIFIED BY 'chess_user';
GRANT ALL PRIVILEGES ON network_chess.* TO `chess_user`@`localhost` WITH GRANT OPTION;

create table user_stat (
	user_id varchar(100),
  	wins int,
    losses int,
  	primary key(user_id)
);