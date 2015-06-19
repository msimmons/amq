create table msg_bridge_to_bus (
   id integer auto_increment not null,
   message longblob not null,
   destination varchar(255) not null,
   created_at datetime,
   updated_at datetime,
   primary key (id)
) ENGINE=InnoDB;

create table msg_bridge_from_bus (
   id integer auto_increment not null,
   message longblob not null,
   destination varchar(255) not null,
   locked_at datetime,
   locked_by varchar(255),
   attempts integer,
   created_at datetime,
   updated_at datetime,
   primary key (id)
) ENGINE=InnoDB;