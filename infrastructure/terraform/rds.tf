resource "aws_db_parameter_group" "no_ssl" {
  name   = "${var.project_name}-${var.environment}-no-ssl"
  family = "postgres16"

  parameter {
    name  = "rds.force_ssl"
    value = "0"
  }

  tags = {
    Name = "${var.project_name}-${var.environment}-no-ssl"
  }
}

resource "aws_db_subnet_group" "main" {
  name       = "${var.project_name}-${var.environment}-db-subnet"
  subnet_ids = aws_subnet.public[*].id

  tags = {
    Name = "${var.project_name}-${var.environment}-db-subnet"
  }
}

resource "aws_security_group" "rds_public" {
  name        = "${var.project_name}-${var.environment}-rds-public-sg"
  description = "Security group for public RDS access"
  vpc_id      = aws_vpc.main.id

  ingress {
    protocol    = "tcp"
    from_port   = 5432
    to_port     = 5432
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    protocol    = "-1"
    from_port   = 0
    to_port     = 0
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "${var.project_name}-${var.environment}-rds-public-sg"
  }
}

resource "aws_db_instance" "postgres" {
  identifier     = "${var.project_name}-${var.environment}-db"
  engine         = "postgres"
  engine_version = "16.3"
  instance_class = "db.t3.micro"

  allocated_storage     = 20
  max_allocated_storage = 50
  storage_type          = "gp3"

  db_name  = var.db_name
  username = var.db_username
  password = var.db_password

  parameter_group_name   = aws_db_parameter_group.no_ssl.name
  db_subnet_group_name   = aws_db_subnet_group.main.name
  vpc_security_group_ids = [aws_security_group.rds_public.id]

  multi_az            = false
  publicly_accessible = true
  skip_final_snapshot = true

  backup_retention_period = 7
  deletion_protection     = false

  tags = {
    Name = "${var.project_name}-${var.environment}-db"
  }
}
