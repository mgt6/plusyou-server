create table user_awards (assignedDate datetime not null, timesWon integer not null, award_id bigint, user_id bigint, primary key (award_id, user_id)) ENGINE=InnoDB;
alter table user_awards add index FKD87A18AAB91C6EC3 (user_id), add constraint FKD87A18AAB91C6EC3 foreign key (user_id) references users (id);
alter table user_awards add index FKD87A18AA188980D1 (award_id), add constraint FKD87A18AA188980D1 foreign key (award_id) references awards (id);
