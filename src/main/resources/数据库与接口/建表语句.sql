/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2018/8/6 22:13:07                            */
/*==============================================================*/


drop table if exists wx_user;

drop table if exists wx_address;

drop table if exists wx_goods;

drop table if exists wx_order;

drop table if exists wx_order_goods;

drop table if exists wx_order_pay;



/*==============================================================*/
/* Table: wx_user                                               */
/*==============================================================*/
create table wx_user
(
   id                   varchar(80) not null,
   openid               varchar(50) not null,
   name                 varchar(50) ,
   avatar               varchar(500),
   create_time          datetime default CURRENT_TIMESTAMP,
   update_time          datetime default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   primary key (id),
   unique key (openid)
) ENGINE=INNODB  DEFAULT CHARSET=utf8 ;


/*==============================================================*/
/* Table: wx_address                                            */
/*==============================================================*/
create table wx_address
(
   id                   varchar(80) not null,
   user_id              varchar(80) not null,
   name                 varchar(20) not null,
   mobile               varchar(20) not null ,
   address_area         varchar(200) not null,
   address_detail       varchar(200) not null ,
   last_used            smallint default 0,
   create_time          datetime default CURRENT_TIMESTAMP,
   update_time          datetime default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   primary key (id)
) ENGINE=INNODB  DEFAULT CHARSET=utf8 ;


/*==============================================================*/
/* Table: wx_goods                                              */
/*==============================================================*/
create table wx_goods
(
   id                   varchar(80) not null,
   title                varchar(50) not null,
   photo                varchar(200) not null,
   wholesale_price      decimal(18,2) not null,
   retail_price         decimal(18,2) not null,
   num                  int not null,
   address              varchar(200),
   in_banner            smallint not null default 0,
   create_time          datetime default CURRENT_TIMESTAMP,
   update_time          datetime default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   primary key (id)
) ENGINE=INNODB  DEFAULT CHARSET=utf8 ;


/*==============================================================*/
/* Table: wx_order                                              */
/*==============================================================*/
create table wx_order
(
   id                   varchar(80) not null,
   user_id              varchar(80) not null,
   name                 varchar(20) not null,
   mobile               varchar(20) not null,
   address_area         varchar(200) not null,
   address_detail       varchar(200) not null,
   order_code           varchar(50) not null,
   goods_total          decimal(18,2) not null,
   order_note           varchar(500),
   status               int not null ,
   status_desc          varchar(200) ,
   create_time          datetime default CURRENT_TIMESTAMP ,
   update_time          datetime default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   primary key (id)
) ENGINE=INNODB  DEFAULT CHARSET=utf8 ;

/*==============================================================*/
/* Table: wx_order_goods                                        */
/*==============================================================*/
create table wx_order_goods
(
   id                   varchar(80) not null,
   user_id              varchar(80) not null,
   order_id             varchar(80) not null,
   goods_id             varchar(80) not null,
   goods_title          varchar(50),
   goods_photo          varchar(200),
   goods_wholesale_price      decimal(18,2) not null,
   goods_retail_price         decimal(18,2) not null,
   goods_buy_num              int not null,
   goods_address              varchar(200),
   create_time          datetime default CURRENT_TIMESTAMP ,
   update_time          datetime default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   primary key (id)
) ENGINE=INNODB  DEFAULT CHARSET=utf8 ;


/*==============================================================*/
/* Table: wx_order_pay                                          */
/*==============================================================*/
create table wx_order_pay
(
   id                   varchar(80) not null,
   order_id             varchar(80),
   order_code           varchar(80),
   openid              varchar(80) ,
   channel_no           varchar(80) ,
   ip                   varchar(80) ,
   money                decimal(18,2) ,
   pay_status           int default 0 comment '֧0-unpaid, 1-paid success, 2-paid fail, 3-refund success, 4-refund fail',
   create_time          datetime default CURRENT_TIMESTAMP ,
   update_time          datetime default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   primary key (id)
) ENGINE=INNODB  DEFAULT CHARSET=utf8 ;


/*==============================================================*/
/* Table: wx_admin                                          */
/*==============================================================*/
create table wx_admin
(
   id                   varchar(80) not null,
   name                 varchar(80),
   password             varchar(500),
   create_time          datetime default CURRENT_TIMESTAMP ,
   update_time          datetime default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   primary key (id)
) ENGINE=INNODB  DEFAULT CHARSET=utf8 ;


/*==============================================================*/
/* Table: wx_cache                                          */
/*==============================================================*/
create table wx_cache
(
   id                   int not null auto_increment,
   key_name             varchar(80),
   value                varchar(500),
   create_time          datetime default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   timeout              int not null,
   primary key (id)
) ENGINE=INNODB  DEFAULT CHARSET=utf8 ;

alter table wx_cache add UNIQUE KEY(key_name);