

drop database if exists proje;
create database proje;

use proje;

Create table account(account_id varchar(50), manager boolean not null default 0, password varchar(20), primary key(account_id), UNIQUE (account_id));

Create table administrator (account_id varchar(50) NOT NULL, primary key(account_id),UNIQUE (account_id), foreign key (account_id) references account(account_id));

Create table Clothing(CID int not null auto_increment, brand varchar(50),bid_increment float not null,cur_price float not null, start_price float not null, name varchar(50), primary key(CID));
alter table Clothing auto_increment=1;


Create table Bids(Bid_ID int AUTO_INCREMENT, price float, manager boolean not null default 0, winner int default 0, upper_limit float, account_id varchar(50) NOT NULL, CID int NOT NULL, bidincrement float default 0, primary key(Bid_ID, account_id, CID), foreign key(account_id) references account(account_id) on delete cascade,
 foreign key (CID) references Clothing(CID)  on delete cascade);

Create table bottoms(CID int not null,category varchar(50) not null, size varchar(20) not null, primary key (CID),foreign key (CID) references Clothing(CID) on delete cascade);

Create table buyer (account_id varchar(50) not null, primary key(account_id),UNIQUE (account_id), foreign key (account_id) references Account(account_id) on delete cascade);

create table Checks(account_id varchar(50) not null, CID int not null, UNIQUE (account_id), foreign key (account_id) references Account(account_id) on delete cascade,foreign key (CID) references Clothing(CID) on delete cascade);

Create table customer_representative (account_id varchar(50) not null, primary key(account_id), UNIQUE (account_id), foreign key (account_id) references Account(account_id) on delete cascade);

Create table Seller (account_id varchar(50) not null, primary key(account_id),UNIQUE (account_id), foreign key (account_id) references Account(account_id) on delete cascade);

CREATE TABLE Sells(account_id varchar(50) not null, CID int not null, Minimum float not null, Start_date datetime not null, End_date datetime not null, PRIMARY KEY (account_id, CID), FOREIGN KEY (account_id) REFERENCES Seller(account_id) on delete cascade, FOREIGN KEY (CID) REFERENCES Clothing(CID) on delete cascade);

CREATE TABLE Shoes (CID int not null, category varchar(50) not null, Size varchar(20) not null, PRIMARY KEY (CID), FOREIGN KEY (CID) REFERENCES Clothing(CID) on delete cascade);

CREATE TABLE Tops (CID int, category varchar(50) not null, size varchar(20) not null, PRIMARY KEY (CID), FOREIGN KEY (CID) REFERENCES Clothing(CID) on delete cascade);

CREATE TABLE Watching_Alert (Info varchar(50), account_id varchar(50), PRIMARY KEY (account_id), UNIQUE (account_id),  FOREIGN KEY (account_id) REFERENCES Seller(account_id) on delete cascade);

Create Table Manage(account_id varchar(50), CID int not null, foreign key (account_id) references Account(account_id),foreign key (CID) references Clothing(CID) on delete cascade);

create table QA(Q varchar(100) primary key, A varchar(100));



INSERT INTO account
VALUES      ('Admin','1', 'Admin' );             
INSERT INTO administrator
VALUES      ('Admin');

INSERT INTO account
VALUES      ('CR','1', 'CR' );             
INSERT INTO customer_representative
VALUES      ('CR');
