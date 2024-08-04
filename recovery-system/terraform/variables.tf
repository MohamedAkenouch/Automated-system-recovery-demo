variable "db_engine" {
  type = string
}

variable "db_name" {
  type = string
}

variable "db_user" {
  type = string
}

variable "db_password" {
  type = string
}

variable "db_port" {
  default = 5432
  type = number
}

variable "db_volume_name" {
  type = string
}