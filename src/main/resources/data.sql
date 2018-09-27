insert into user (id, email, password, nickname, regdate ,user_status) VALUES (1, 'noriming2@gmail.com', '{bcrypt}$2a$10$qS48/8nM2fSagy1di.whF.tutE/VZ9/wwOkGBcm.Ty8mOKLfwpv/G', '아이스베어', now(),'NORMAL');
insert into user (id, email, password, nickname, regdate ,user_status) VALUES (2, 'yeonjeong.serena@gmail.com', '{bcrypt}$2a$10$qS48/8nM2fSagy1di.whF.tutE/VZ9/wwOkGBcm.Ty8mOKLfwpv/G', '갈색곰', now(),'NORMAL');
insert into user (id, email, password, nickname, regdate ,user_status) VALUES (3, 'test3', '{bcrypt}$2a$10$qS48/8nM2fSagy1di.whF.tutE/VZ9/wwOkGBcm.Ty8mOKLfwpv/G', '판다', now(),'NORMAL');
insert into user (id, email, password, nickname, regdate ,delete_date ,user_status) VALUES (4, 'bee@naver.com', '{bcrypt}$2a$10$qS48/8nM2fSagy1di.whF.tutE/VZ9/wwOkGBcm.Ty8mOKLfwpv/G', '땡벌', now(),now(),'DELETE');

insert into user_roles(id, role_state, user_id) VALUES (1, 'USER', 1);
insert into user_roles(id, role_state, user_id) VALUES (2, 'ADMIN', 1);
insert into user_roles(id, role_state, user_id) VALUES (3, 'USER', 2);
insert into user_roles(id, role_state, user_id) VALUES (4, 'USER', 3);
insert into user_roles(id, role_state, user_id) VALUES (5, 'USER', 4);

insert into channel(id, name, type, regdate) VALUES (1, 'general', 'PUBLIC', now());

insert into channel_user(id, user_id, channel_id, first_read_id, regdate, last_read_cnt) VALUES (1, 1, 1, 1, now(), 0);
insert into channel_user(id, user_id, channel_id, first_read_id, regdate, last_read_cnt) VALUES (2, 4, 1, 1, now(), 0);

insert into oauth_info(id,access_token,sub,name,picture,email,user_id)
VALUES (1,'ya29.GlvXBbqz71mWDrxUVDqC2AiZTPHxHMIdch69Ml9NFeKmr9qfVL9g22QA64slm7wS4fiBqW7x24tbGBrMXlj0AqvStYEvPmQMYc4r7V4XgM_R-y9wOoBg-ZVKKss3','105547134406045421901','soso','https://lh3.googleusercontent.com/-EpeCRDESczo/AAAAAAAAAAI/AAAAAAAAA2A/dnUWLQwBFh4/photo.jpg','noriming2@gmail.com',1);
