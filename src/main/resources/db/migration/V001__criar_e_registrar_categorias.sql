create table categoria(
	codigo bigint(20)primary key not null auto_increment,
	nome varchar(50) not null
)engine=InnoDB default charset=utf8mb4;

insert into categoria (nome) values ('Lazer');
insert into categoria (nome) values ('Alimentação');
insert into categoria (nome) values ('Supermercado');
insert into categoria (nome) values ('Farmácia');
insert into categoria (nome) values ('Outros');