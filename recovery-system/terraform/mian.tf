provider "docker" {}

module "db" {
  source           = "db"
  db_image         = "${var.db_engine}:latest"
  db_name          = var.db_name
  db_user          = var.db_user
  db_password      = var.db_password
  db_port          = var.db_port
  db_volume_name   = "${var.db_engine}_${var.db_name}_volume"
}

output "db_container_name" {
  value = module.db.container_name
}

output "db_volume_name" {
  value = module.db.volume_name
}

locals {
  db_url = var.db_engine == "mysql" ?
    format("mysql://%s:%d/%s", module.db.db_id_address, var.db_name) :
    format("postgresql://%s:%d/%s", module.db.db_id_address, var.db_port, var.db_name)
}

output "db_url" {
  value = local.db_url
}