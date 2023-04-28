# PassCraft

PassCraft is a simple authentication plugin which includes password authentication and discord authentication (not as 2FA yet).

It has been developed to solve a common problem with offline-mode minecraft servers : the identity theft.

The plugin uses a SQL database to store passwords and linked discord accounts.
You can use it with a local database by default (SQLite) or with a MySQL server
(see the [configuration section](#configuration)).

## Configuration

<details>
    <summary><b>Authentication</b></summary>

| key                                                 |   value   | default | description                                                                    |
|-----------------------------------------------------|:---------:|:-------:|--------------------------------------------------------------------------------|
| `authentication.chest-interface.enabled`            | `boolean` | `true`  | Enable the authentication chest-interface.                                     |
| `authentication.premium.enabled`                    | `boolean` | `false` | Enable premium accounts automatic authentication (<b>not recommended yet</b>). |
| `authentication.discord.enabled`                    | `boolean` | `false` | Enable discord authentication service.                                         |
| `authentication.discord.bot.token`                  | `string`  |  None   | Token of the bot used by the plugin for the discord authentication service.    |
| `authentication.discord.bot.requests-timeout-delay` |   `int`   |  `120`  |                                                                                |
| `authentication.unregistered-kick-delay`            |   `int`   |   `2`   |                                                                                |
| `authentication.reconnection-delay`                 |   `int`   |  `45`   |                                                                                |
| `authentication.authentication-timeout-kick-delay`  |   `int`   |  `120`  |                                                                                |

</details>

<br>

<details>
    <summary><b>Database</b></summary>

| key                                                     | value | description |
|---------------------------------------------------------|-------|-------------|
| database.system                                         |       |             |
| database.sqlite.path                                    |       |             |
| database.mysql.hostname                                 |       |             |
| database.mysql.port                                     |       |             |
| database.mysql.database-name                            |       |             |
| database.mysql.username                                 |       |             |
| database.mysql.password                                 |       |             |
| database.tables.passwords.name                          |       |             |
| database.tables.passwords.columns.user                  |       |             |
| database.tables.passwords.columns.password              |       |             |
| database.tables.discords.name                           |       |             |
| database.tables.discords.columns.user                   |       |             |
| database.tables.discords.columns.discord                |       |             |

</details>

<br>

<details>
    <summary><b>Messages</b></summary>

| key                                                     | value | description |
|---------------------------------------------------------|-------|-------------|
| messages.must-authenticate-notify                       |       |             |
| messages.invalid-password-format                        |       |             |
| messages.not-password-registered                        |       |             |
| messages.discord-not-linked                             |       |             |
| messages.discord-user-not-found                         |       |             |
| messages.already-password-registered                    |       |             |
| messages.discord-already-linked                         |       |             |
| messages.discord-already-used                           |       |             |
| messages.register-success                               |       |             |
| messages.register-failed                                |       |             |
| messages.discord-linking-success                        |       |             |
| messages.discord-unlinking-success                      |       |             |
| messages.discord-linking-refused                        |       |             |
| messages.discord-linking-failed                         |       |             |
| messages.discord-unlinking-failed                       |       |             |
| messages.discord-linking-password-registration-required |       |             |
| messages.discord-request                                |       |             |
| messages.discord-request-timed-out                      |       |             |
| messages.unregister-success                             |       |             |
| messages.unregister-failed                              |       |             |
| messages.unregister-kick                                |       |             |
| messages.login-success                                  |       |             |
| messages.already-logged                                 |       |             |
| messages.login-failed                                   |       |             |
| messages.wrong-password                                 |       |             |
| messages.discord-auth-disabled                          |       |             |
| messages.authentication-timed-out-kick                  |       |             |

</details>

<br>

<details>
    <summary><b>Configuration tokens</b></summary>

| key                                                     | value | description |
|---------------------------------------------------------|-------|-------------|
| config-tokens                                           |       |             |

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