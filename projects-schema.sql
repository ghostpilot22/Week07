DROP TABLE IF EXISTS material;
DROP TABLE IF EXISTS step;
DROP TABLE IF EXISTS project_category;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS project;

CREATE TABLE project (
	project_id int NOT NULL PRIMARY KEY AUTO_INCREMENT,
	project_name varchar(128) NOT NULL,
	estimated_hours decimal(7,2),
	actual_hours decimal(7,2),
	difficulty int,
	notes text
	);

CREATE TABLE category (
	category_id int NOT NULL PRIMARY KEY AUTO_INCREMENT,
	category_name varchar(128) NOT NULL
	);

CREATE TABLE project_category (
	project_id int NOT NULL,
	category_id int NOT NULL,
	FOREIGN KEY (project_id) 
		REFERENCES project(project_id) ON DELETE CASCADE,
	FOREIGN KEY (category_id) 
		REFERENCES category(category_id) ON DELETE CASCADE
	);

CREATE TABLE material (
	material_id int NOT NULL PRIMARY KEY AUTO_INCREMENT,
	project_id int NOT NULL,
	material_name varchar(128) NOT NULL,
	num_required int,
	cost decimal(7,2),
	FOREIGN KEY (project_id) 
		REFERENCES project(project_id) ON DELETE CASCADE
	);

CREATE TABLE step (
	step_id int NOT NULL PRIMARY KEY AUTO_INCREMENT,
	project_id int NOT NULL,
	step_text text NOT NULL,
	step_order int NOT NULL,
	FOREIGN KEY (project_id) 
		REFERENCES project(project_id) ON DELETE CASCADE
	);