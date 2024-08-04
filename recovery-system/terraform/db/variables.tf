variable "db_image" {
  description = "The Docker image for the database"
  type        = string
}

variable "db_name" {
  description = "The name of the database"
  type        = string
}

variable "db_user" {
  description = "The database user"
  type        = string
}

variable "db_password" {
  description = "The database password"
  type        = string
}

variable "db_port" {
  description = "The port for the database"
  type        = number
  default     = 5432  # Default port for PostgreSQL
}

variable "db_volume_name" {
  description = "The name of the Docker volume for database storage"
  type        = string
}
