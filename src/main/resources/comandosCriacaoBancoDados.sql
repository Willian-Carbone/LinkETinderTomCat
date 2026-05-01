


CREATE TABLE "usuario" (
   "id" SERIAL PRIMARY KEY,
   "nome" VARCHAR(50) NOT NULL,
   "email"  VARCHAR(50) NOT NULL UNIQUE,
   "cep" VARCHAR(8) NOT NULL,
   "estado" VARCHAR(2) NOT NULL,
    "descricao" TEXT
 );

 CREATE TABLE "candidato" (
   "cpf" VARCHAR(11) PRIMARY KEY,
   "idade" INT NOT NULL,
   "candidato_id" INT NOT NULL
 );

 CREATE TABLE "empresa" (
   "cnpj" VARCHAR(14) PRIMARY KEY,
   "pais" VARCHAR(15) NOT NULL,
   "empresa_id" INT NOT NULL
 );

 CREATE TABLE "vaga" (
   "id" SERIAL PRIMARY KEY,
   "nome" VARCHAR(100) NOT NULL,
   "descricao" TEXT NOT NULL,
   "contratante" VARCHAR(14) NOT NULL
 );

 CREATE TABLE "especialidade_vaga" (
   "id" SERIAL PRIMARY KEY,
   "vaga" INT NOT NULL,
   "especialidade" VARCHAR(5) NOT NULL
 );

 CREATE TABLE "curtida" (
   "id" SERIAL PRIMARY KEY,
   "vaga" INT NOT NULL,
   "candidato" VARCHAR(11) NOT NULL
 );

 CREATE TABLE "estado" (
   "sigla" VARCHAR(2) PRIMARY KEY,
   "nome" VARCHAR(20) UNIQUE NOT NULL
 );

 CREATE TABLE "especialidade" (
   "sigla" VARCHAR(5) PRIMARY KEY NOT NULL,
   "nome" VARCHAR(20) UNIQUE NOT NULL
 );

 CREATE TABLE "especialidade_usuario" (
   "id" SERIAL PRIMARY KEY,
   "especialidade" VARCHAR(5) NOT NULL,
   "usuario" INT NOT NULL
 );

 CREATE TABLE "matchs" (
   "id" SERIAL PRIMARY KEY,
   "empresa" VARCHAR(14) NOT NULL,
   "candidato" VARCHAR(11) NOT NULL,
   "vaga" INT NOT NULL
 );

 ALTER TABLE "usuario" ADD FOREIGN KEY ("estado") REFERENCES "estado" ("sigla") ;

 ALTER TABLE "candidato" ADD FOREIGN KEY ("candidato_id") REFERENCES "usuario" ("id") ;

 ALTER TABLE "empresa" ADD FOREIGN KEY ("empresa_id") REFERENCES "usuario" ("id") ;

 ALTER TABLE "vaga" ADD FOREIGN KEY ("contratante") REFERENCES "empresa" ("cnpj") ;

 ALTER TABLE "especialidade_vaga" ADD FOREIGN KEY ("vaga") REFERENCES "vaga" ("id") ;

 ALTER TABLE "especialidade_vaga" ADD FOREIGN KEY ("especialidade") REFERENCES "especialidade" ("sigla") ;

 ALTER TABLE "curtida" ADD FOREIGN KEY ("vaga") REFERENCES "vaga" ("id") ;

 ALTER TABLE "curtida" ADD FOREIGN KEY ("candidato") REFERENCES "candidato" ("cpf") ;

 ALTER TABLE "especialidade_usuario" ADD FOREIGN KEY ("especialidade") REFERENCES "especialidade" ("sigla") ;

 ALTER TABLE "especialidade_usuario" ADD FOREIGN KEY ("usuario") REFERENCES "usuario" ("id") ;

 ALTER TABLE "matchs" ADD FOREIGN KEY ("empresa") REFERENCES "empresa" ("cnpj") ;

 ALTER TABLE "matchs" ADD FOREIGN KEY ("candidato") REFERENCES "candidato" ("cpf") ;

 ALTER TABLE "matchs" ADD FOREIGN KEY ("vaga") REFERENCES "vaga" ("id") ;





INSERT INTO estado("sigla","nome") VALUES ('AC','Acre');
INSERT INTO estado("sigla","nome") VALUES ('AL','Alagoas');
INSERT INTO estado("sigla","nome") VALUES ('AP','Amapa');
INSERT INTO estado("sigla","nome") VALUES ('AM','Amazonas');
INSERT INTO estado("sigla","nome") VALUES ('BH','Bahia');
INSERT INTO estado("sigla","nome") VALUES ('CE','Ceara');
INSERT INTO estado("sigla","nome") VALUES ('DF','Distrito Federal');
INSERT INTO estado("sigla","nome") VALUES ('ES','Espirito Santo');
INSERT INTO estado("sigla","nome") VALUES ('GO','Goias');
INSERT INTO estado("sigla","nome") VALUES ('MA','maranhão');
INSERT INTO estado("sigla","nome") VALUES ('MT','Mato Grosso');
INSERT INTO estado("sigla","nome") VALUES ('MS','Mato Grosso do Sul');
INSERT INTO estado("sigla","nome") VALUES ('MG','Minas Gerais');
INSERT INTO estado("sigla","nome") VALUES ('PA','Pará');
INSERT INTO estado("sigla","nome") VALUES ('PB','Paraíba');
INSERT INTO estado("sigla","nome") VALUES ('PR','Paraná');
INSERT INTO estado("sigla","nome") VALUES ('PE','Pernambuco');
INSERT INTO estado("sigla","nome") VALUES ('PI','Piaui');
INSERT INTO estado("sigla","nome") VALUES ('RJ','Rio de Janeiro');
INSERT INTO estado("sigla","nome") VALUES ('RN','Rio Grande do Norte');
INSERT INTO estado("sigla","nome") VALUES ('RS','Rio Grande do Sul');
INSERT INTO estado("sigla","nome") VALUES ('RO','Rondônia');
INSERT INTO estado("sigla","nome") VALUES ('RR','Roraima');
INSERT INTO estado("sigla","nome") VALUES ('SC','Santa Catarina');
INSERT INTO estado("sigla","nome") VALUES ('SP','São Paulo');
INSERT INTO estado("sigla","nome") VALUES ('SE','Sergipe');
INSERT INTO estado("sigla","nome") VALUES ('TO','Tocantins');


INSERT INTO especialidade ("sigla","nome") VALUES ('PT','Phyton');
INSERT INTO especialidade ("sigla","nome") VALUES ('JAV','Java');
INSERT INTO especialidade ("sigla","nome") VALUES ('ANG','Angular');
INSERT INTO especialidade ("sigla","nome") VALUES ('SPR','Spring');
INSERT INTO especialidade ("sigla","nome") VALUES ('HT','Html');
INSERT INTO especialidade ("sigla","nome") VALUES ('CS','Css');
INSERT INTO especialidade ("sigla","nome") VALUES ('C','C++');


