# PassCraft

PassCraft is a simple authentication plugin which includes password authentication and discord authentication (not as 2FA yet).

It has been developed to solve a common problem with offline-mode minecraft servers : the identity theft.

The plugin uses a SQL database to store passwords and linked discord accounts.
You can use it with a local database by default (SQLite) or with a MySQL server
(see the [configuration section](#configuration)).

## Configuration

<details>
    <summary style="cursor: pointer;"><b>Authentication</b></summary>

<br>

| key                                                 |   value   | default | description                                                                         |
|-----------------------------------------------------|:---------:|:-------:|-------------------------------------------------------------------------------------|
| `authentication.chest-interface.enabled`            | `boolean` | `true`  | Enable the authentication chest-interface.                                          |
|                                                     |           |         |                                                                                     |
| `authentication.premium.enabled`                    | `boolean` | `false` | Enable premium accounts automatic authentication.                                   |
|                                                     |           |         |                                                                                     |
| `authentication.discord.enabled`                    | `boolean` | `false` | Enable discord authentication service.                                              |
| `authentication.discord.bot.token`                  | `string`  | `None`  | Token of the bot used by the plugin for the discord authentication service.         |
| `authentication.discord.bot.requests-timeout-delay` |   `int`   |  `120`  | Defines the delay before a request from the plugin discord bot expired / timed out. |
|                                                     |           |         |                                                                                     |
| `authentication.unregistered-kick-delay`            |   `int`   |   `2`   | Delay before player who has just unregistered will be kicked from the server.       |
| `authentication.reconnection-delay`                 |   `int`   |  `45`   | Delay before a player has to login again the next time.                             |
| `authentication.authentication-timeout-kick-delay`  |   `int`   |  `120`  | Maximum delay a player has to login.                                                |

</details>

<br>

<details>
    <summary style="cursor: pointer;"><b>Database</b></summary>

<br>

| key                                          |        value        |           default           | description                                |
|----------------------------------------------|:-------------------:|:---------------------------:|--------------------------------------------|
| `database.system`                            | `sqlite` \| `mysql` |          `sqlite`           | Which database system the plugin will use. |
|                                              |                     |                             |                                            |
| `database.sqlite.path`                       |      `string`       | `%datafolder%/passcraft.db` |                                            |
|                                              |                     |                             |                                            |
| `database.mysql.hostname`                    |      `string`       |           *empty*           |                                            |
| `database.mysql.port`                        |        `int`        |           `3306`            |                                            |
| `database.mysql.database-name`               |      `string`       |           *empty*           |                                            |
| `database.mysql.username`                    |      `string`       |           *empty*           |                                            |
| `database.mysql.password`                    |      `string`       |           *empty*           |                                            |
|                                              |                     |                             |                                            |
| `database.tables.passwords.name`             |      `string`       |         `passcraft`         |                                            |
| `database.tables.passwords.columns.user`     |      `string`       |           `user`            |                                            |
| `database.tables.passwords.columns.password` |      `string`       |         `password`          |                                            |
|                                              |                     |                             |                                            |
| `database.tables.discords.name`              |      `string`       |         `passcraft`         |                                            |
| `database.tables.discords.columns.user`      |      `string`       |           `user`            |                                            |
| `database.tables.discords.columns.discord`   |      `string`       |          `discord`          |                                            |

</details>

<br>

<details>
    <summary style="cursor: pointer;"><b>Messages</b></summary>

<br>

| key                                                       |  value   |    default    | description |
|-----------------------------------------------------------|:--------:|:-------------:|-------------|
| `messages.must-authenticate-notify`                       | `string` | *see in file* |             |
| `messages.invalid-password-format`                        | `string` | *see in file* |             |
| `messages.not-password-registered`                        | `string` | *see in file* |             |
| `messages.discord-not-linked`                             | `string` | *see in file* |             |
| `messages.discord-user-not-found`                         | `string` | *see in file* |             |
| `messages.already-password-registered`                    | `string` | *see in file* |             |
| `messages.discord-already-linked`                         | `string` | *see in file* |             |
| `messages.discord-already-used`                           | `string` | *see in file* |             |
| `messages.register-success`                               | `string` | *see in file* |             |
| `messages.register-failed`                                | `string` | *see in file* |             |
| `messages.discord-linking-success`                        | `string` | *see in file* |             |
| `messages.discord-unlinking-success`                      | `string` | *see in file* |             |
| `messages.discord-linking-refused`                        | `string` | *see in file* |             |
| `messages.discord-linking-failed`                         | `string` | *see in file* |             |
| `messages.discord-unlinking-failed`                       | `string` | *see in file* |             |
| `messages.discord-linking-password-registration-required` | `string` | *see in file* |             |
| `messages.discord-request`                                | `string` | *see in file* |             |
| `messages.discord-request-timed-out`                      | `string` | *see in file* |             |
| `messages.unregister-success`                             | `string` | *see in file* |             |
| `messages.unregister-failed`                              | `string` | *see in file* |             |
| `messages.unregister-kick`                                | `string` | *see in file* |             |
| `messages.login-success`                                  | `string` | *see in file* |             |
| `messages.already-logged`                                 | `string` | *see in file* |             |
| `messages.login-failed`                                   | `string` | *see in file* |             |
| `messages.wrong-password`                                 | `string` | *see in file* |             |
| `messages.discord-auth-disabled`                          | `string` | *see in file* |             |
| `messages.authentication-timed-out-kick`                  | `string` | *see in file* |             |

</details>

<br>

<details>
    <summary style="cursor: pointer;"><b>Configuration tokens</b></summary>

<br>

| key                    | value    | default       | description |
|------------------------|----------|---------------|-------------|
| `config-tokens.prefix` | `string` | *see in file* |             |

</details>

## Features

### Authentication chest-interface

The plugin implements a "chest-interface", which allows to players to type their password to login / register / unregister.

### Automatic premium authentication (in-dev)

THe plugin can detect if the player is using a premium account and force him to join as premium, or be kicked if he is not the owner.

## Contributors

List of contributors :

<div style="float:left;margin:0 10px 10px 0">
    <img align="left" src="https://contrib.rocks/image?repo=one-dev-man/4th" width="24px">
    <a href="https://github.com/one-dev-man/">
        onedevman
    </a>
</div>