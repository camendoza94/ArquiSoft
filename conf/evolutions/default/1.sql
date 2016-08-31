# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table campoentity (
  id                        bigint not null,
  nombre                    varchar(255),
  latitud                   float,
  longitud                  float,
  region_id                 bigint not null,
  jefeproduccion_id         bigint not null,
  jefecampo_id              bigint not null,
  constraint uq_campoentity_jefeproduccion_id unique (jefeproduccion_id),
  constraint uq_campoentity_jefecampo_id unique (jefecampo_id),
  constraint pk_campoentity primary key (id))
;

create table itementity (
  id                        bigint not null,
  quantity                  integer,
  product_id                bigint not null,
  wishlist_id               bigint not null,
  constraint pk_itementity primary key (id))
;

create table medicionentity (
  id                        bigint not null,
  valor                     float,
  fecha                     timestamp,
  sensor_entity_id          bigint not null,
  constraint pk_medicionentity primary key (id))
;

create table pozoentity (
  id                        bigint not null,
  estado                    integer,
  campo_id                  bigint not null,
  constraint pk_pozoentity primary key (id))
;

create table productentity (
  id                        bigint not null,
  name                      varchar(255),
  stock                     integer,
  price                     float,
  available                 boolean,
  constraint pk_productentity primary key (id))
;

create table regionentity (
  id                        bigint not null,
  nombre                    varchar(255),
  area                      float,
  constraint pk_regionentity primary key (id))
;

create table reporteentity (
  id                        bigint not null,
  fecha_generacion          timestamp,
  campo_id                  bigint,
  pozo_id                   bigint,
  constraint pk_reporteentity primary key (id))
;

create table sensorentity (
  id                        bigint not null,
  nombre                    varchar(255),
  tipo_sensor               integer,
  pozo_id                   bigint not null,
  constraint pk_sensorentity primary key (id))
;

create table usuarioentity (
  id                        bigint not null,
  nombre                    varchar(255),
  constraint pk_usuarioentity primary key (id))
;

create table wishlistentity (
  id                        bigint not null,
  username                  varchar(255),
  constraint pk_wishlistentity primary key (id))
;

create sequence Campo;

create sequence Item;

create sequence Medicion;

create sequence Pozo;

create sequence Product;

create sequence Region;

create sequence Reporte;

create sequence Sensor;

create sequence Usuario;

create sequence WishList;

alter table campoentity add constraint fk_campoentity_region_1 foreign key (region_id) references regionentity (id);
create index ix_campoentity_region_1 on campoentity (region_id);
alter table campoentity add constraint fk_campoentity_jefeProduccion_2 foreign key (jefeproduccion_id) references usuarioentity (id);
create index ix_campoentity_jefeProduccion_2 on campoentity (jefeproduccion_id);
alter table campoentity add constraint fk_campoentity_jefeCampo_3 foreign key (jefecampo_id) references usuarioentity (id);
create index ix_campoentity_jefeCampo_3 on campoentity (jefecampo_id);
alter table itementity add constraint fk_itementity_product_4 foreign key (product_id) references productentity (id);
create index ix_itementity_product_4 on itementity (product_id);
alter table itementity add constraint fk_itementity_wishlist_5 foreign key (wishlist_id) references wishlistentity (id);
create index ix_itementity_wishlist_5 on itementity (wishlist_id);
alter table medicionentity add constraint fk_medicionentity_sensor_6 foreign key (sensor_entity_id) references sensorentity (id);
create index ix_medicionentity_sensor_6 on medicionentity (sensor_entity_id);
alter table pozoentity add constraint fk_pozoentity_campo_7 foreign key (campo_id) references campoentity (id);
create index ix_pozoentity_campo_7 on pozoentity (campo_id);
alter table reporteentity add constraint fk_reporteentity_campo_8 foreign key (campo_id) references campoentity (id);
create index ix_reporteentity_campo_8 on reporteentity (campo_id);
alter table reporteentity add constraint fk_reporteentity_pozo_9 foreign key (pozo_id) references pozoentity (id);
create index ix_reporteentity_pozo_9 on reporteentity (pozo_id);
alter table sensorentity add constraint fk_sensorentity_pozo_10 foreign key (pozo_id) references pozoentity (id);
create index ix_sensorentity_pozo_10 on sensorentity (pozo_id);



# --- !Downs

drop table if exists campoentity cascade;

drop table if exists itementity cascade;

drop table if exists medicionentity cascade;

drop table if exists pozoentity cascade;

drop table if exists productentity cascade;

drop table if exists regionentity cascade;

drop table if exists reporteentity cascade;

drop table if exists sensorentity cascade;

drop table if exists usuarioentity cascade;

drop table if exists wishlistentity cascade;

drop sequence if exists Campo;

drop sequence if exists Item;

drop sequence if exists Medicion;

drop sequence if exists Pozo;

drop sequence if exists Product;

drop sequence if exists Region;

drop sequence if exists Reporte;

drop sequence if exists Sensor;

drop sequence if exists Usuario;

drop sequence if exists WishList;

