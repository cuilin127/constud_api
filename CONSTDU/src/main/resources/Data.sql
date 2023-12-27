insert into constud.role (role_name) values('ROLE_USER');
insert into constud.role (role_name) values('ROLE_ADMIN');


insert into constud.user (email, first_name, last_name ,password, phone_number,ROLE_ID)
values ('cuilin@sheridan.ca','Lin','Cui','123456','6476676667',1);

insert into constud.user (email, first_name, last_name ,password, phone_number,ROLE_ID)
values ('muyuan@sheridan.ca','muyuan','Cui','12345678','6476676667',1);