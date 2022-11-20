---
layout: page
title: Commands
nav_order: 1
---

# Installation

## Requirements

- [Spigot](https://www.spigotmc.org/)/[Paper](https://papermc.io/)
- MySQL (>= 8) database
  - Execute the SQL in [schema.sql](https://github.com/ericgrandt/TotalEconomyPaper/blob/master/src/main/resources/schema.sql) to setup your schema (NOTE: This will be automated in a future release)

## Step-by-step

1. Move the `TotalEconomy-x.y.z.jar` into the `plugins` folder
2. Start the server to generate the configuration file
   - Optionally, create the configuration beforehand using [config.yml](https://github.com/ericgrandt/TotalEconomyPaper/blob/master/src/main/resources/config.yml) as a reference
3. Update the configuration file with your database information
   - For the database url, follow this structure: `jdbc:mysql://[host]:[port]/[database_name]`
4. Restart the server