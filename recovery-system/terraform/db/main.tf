# Create a Docker volume for persistent storage
resource "docker_volume" "db_volume" {
  name = var.db_volume_name
}

# Create a Docker container for the database
resource "docker_container" "database" {
  name  = "${var.db_image}_${var.db_name}_database"
  image = var.db_image

  # Set environment variables for the database
  env = [
    "DB_USER=${var.db_user}",
    "DB_PASSWORD=${var.db_password}",
    "DB_NAME=${var.db_name}",
  ]

  # Mount the volume to the container
  mount {
    target = "/var/lib/database/data"
    source = docker_volume.db_volume.name
    type   = "volume"
  }

  # Expose the container port
  ports {
    internal = var.db_port
    external = var.db_port
  }
}