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

create table medicionentity (
  id                        bigint not null,
  valor                     float,
  fecha                     timestamp,
  sensor_id                 bigint not null,
  constraint pk_medicionentity primary key (id))
;

create table pozoentity (
  id                        bigint not null,
  estado                    integer,
  campo_id                  bigint not null,
  constraint pk_pozoentity primary key (id))
;

create table regionentity (
  id                        bigint not null,
  nombre                    varchar(255),
  area                      float,
  constraint pk_regionentity primary key (id))
;

create table sensorentity (
  id                        bigint not null,
  nombre                    varchar(255),
  tipo_sensor               integer,
  constraint pk_sensorentity primary key (id))
;

create table usuarioentity (
  id                        bigint not null,
  nombre                    varchar(255),
  constraint pk_usuarioentity primary key (id))
;

create sequence Campo;

create sequence Medicion;

create sequence Pozo;

create sequence Region;

create sequence Sensor;

create sequence Usuario;

alter table campoentity add constraint fk_campoentity_region_1 foreign key (region_id) references regionentity (id);
create index ix_campoentity_region_1 on campoentity (region_id);
alter table campoentity add constraint fk_campoentity_jefeProduccion_2 foreign key (jefeproduccion_id) references usuarioentity (id);
create index ix_campoentity_jefeProduccion_2 on campoentity (jefeproduccion_id);
alter table campoentity add constraint fk_campoentity_jefeCampo_3 foreign key (jefecampo_id) references usuarioentity (id);
create index ix_campoentity_jefeCampo_3 on campoentity (jefecampo_id);
alter table medicionentity add constraint fk_medicionentity_sensor_4 foreign key (sensor_id) references sensorentity (id);
create index ix_medicionentity_sensor_4 on medicionentity (sensor_id);
alter table pozoentity add constraint fk_pozoentity_campo_5 foreign key (campo_id) references campoentity (id);
create index ix_pozoentity_campo_5 on pozoentity (campo_id);



# --- !Downs

drop table if exists campoentity cascade;

drop table if exists medicionentity cascade;

drop table if exists pozoentity cascade;

drop table if exists regionentity cascade;

drop table if exists sensorentity cascade;

drop table if exists usuarioentity cascade;

drop sequence if exists Campo;

drop sequence if exists Medicion;

drop sequence if exists Pozo;

drop sequence if exists Region;

drop sequence if exists Sensor;

drop sequence if exists Usuario;

