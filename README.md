# AdminChat

AdminChat is multi-channel admin-only chat system for Paper servers, designed for staff communication with hierarchical
channels, configurable permissions, and spam prevention.

![Paper](https://img.shields.io/badge/Paper-1.21.4-green?logo=paper&logoColor=white)
[![MIT License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Latest Release](https://img.shields.io/github/v/release/Chalwk/AdminChat?sort=semver)](https://github.com/Chalwk/AdminChat/releases/latest)

## Features

- **Multi-Channel System**: Separate channels for different staff roles (admin, mod, trial)
- **Channel Toggle Mode**: Set a default channel for continuous use without prefixing every message
- **Secure Communication**: Admin-only chat visible only to authorized staff
- **Toggle Visibility**: Individual players can hide/show admin chat
- **Cooldown System**: Configurable spam prevention
- **Custom Formatting**: Fully customizable message appearance per channel
- **Sound Notifications**: Channel-specific sound alerts
- **Join/Quit Notifications**: Staff join and leave announcements
- **Permission Hierarchy**: Granular permissions for each channel
- **Admin Management**: Reload config and manage player visibility

## Commands

### Chat Commands

- `/achat <message>` or `/ac <message>` - Send message to default channel (or toggled channel if active)
- `/achat <channel> <message>` - Send message to specific channel
- `/achat <channel>` - Toggle channel mode for continuous use (e.g., `/achat admin`)
- `/achat off` - Exit channel toggle mode

### Toggle Commands

- `/achat toggle` - Toggle admin chat visibility for yourself
- `/achat toggle <player>` - Toggle admin chat visibility for another player (admin only)

### Admin Commands

- `/adminchat reload` - Reload the plugin configuration
- `/achat reload` - Alternative reload command (admin only)

## Permissions

- `adminchat.use` - Allows using admin chat (default: op)
- `adminchat.admin` - Access to admin management commands (default: op)
- `adminchat.channel.mod` - Access to mod channel (default: op)
- `adminchat.channel.admin` - Access to admin channel (default: op)
- `adminchat.channel.trial` - Access to trial channel (default: op)
- `adminchat.*` - Wildcard permission for all AdminChat permissions

## Channel Toggle System

The new channel toggle feature allows you to set a default channel for continuous use:

1. **Enable toggle mode**: `/achat admin` (or `mod`/`trial`)
    - Your future messages will automatically go to that channel
    - You'll see: "You are now in admin chat mode. Your messages will be sent to this channel. Type `/achat off` to
      exit."

2. **Send messages in toggle mode**: Simply type your message without the channel prefix
    - Example: "Hello, world" → Sends to the admin channel

3. **Exit toggle mode**: `/achat off`
    - Returns to normal mode where you need to specify the channel for each message

## Configuration

The plugin generates a `config.yml` file with the following options:

### General Settings

```yaml
# Default channel when no channel specified
default_channel: "admin"

# Message cooldown in seconds to prevent spam
cooldown: 2
```

### Channel Configuration

```yaml
channels:
  mod:
    permission: "adminchat.channel.mod"
    format: "&8[&9Mod&8] &b{sender}&8: &f{message}"
    prefix: "&8[&9Mod&8]"
    sound:
      enabled: true
      type: BLOCK_NOTE_BLOCK_PLING
      volume: 0.5
      pitch: 1.5

  admin:
    permission: "adminchat.channel.admin"
    format: "&8[&cAdmin&8] &c{sender}&8: &f{message}"
    prefix: "&8[&cAdmin&8]"
    sound:
      enabled: true
      type: BLOCK_NOTE_BLOCK_PLING
      volume: 0.5
      pitch: 1.8

  trial:
    permission: "adminchat.channel.trial"
    format: "&8[&eTrial&8] &e{sender}&8: &f{message}"
    prefix: "&8[&eTrial&8]"
    sound:
      enabled: false
      type: BLOCK_NOTE_BLOCK_PLING
      volume: 0.5
      pitch: 1.2
```

### Messages

```yaml
messages:
  no_permission: "&cYou don't have permission to use this channel!"
  no_channel: "&cChannel not found!"
  toggled_on: "&aAdmin chat visibility enabled!"
  toggled_off: "&cAdmin chat visibility disabled!"
  toggled_for: "&aAdmin chat visibility {state} for {player}!"
  cooldown: "&cPlease wait {seconds} seconds before sending another message!"
  reloaded: "&aConfiguration reloaded!"
  usage: "&eUsage: /achat [channel] [message] or /achat [channel] (to toggle) or /achat toggle"
  usage_toggled: "&eYou are in {channel} chat mode. Just type your message, or use /achat off to exit."
  channel_on: "&aYou are now in {channel} chat mode. Your messages will be sent to this channel. Type /achat off to exit."
  channel_off: "&cYou have exited {channel} chat mode."
  player_not_found: "&cPlayer not found!"
```

### Notifications

```yaml
notifications:
  join_notification: true
  join_message: "&7[&a+&7] &f{player} &7has joined the server"
  quit_notification: true
  quit_message: "&7[&c-&7] &f{player} &7has left the server"
```

### Available Placeholders

- `{sender}` - The player who sent the message
- `{channel}` - The channel name
- `{message}` - The message content
- `{player}` - Player name (for notifications)
- `{state}` - Enabled/disabled state
- `{seconds}` - Cooldown seconds remaining

## Usage Examples

### Basic Usage

```bash
# Send message to default channel
/achat Meeting in 5 minutes

# Send message to specific channel
/achat mod Can someone check the spawn area?

# Toggle channel mode for continuous use
/achat admin
> "You are now in admin chat mode..."

# Now messages go to admin channel automatically
Hello team, let's start the meeting
> [Admin] YourName: Hello team, let's start the meeting

# Exit toggle mode
/achat off
> "You have exited admin chat mode."
```

### Admin Usage

```bash
# Toggle visibility for another player
/achat toggle Notch

# Reload configuration
/adminchat reload

# Use alternate command alias
/ac Hello staff team!

# Toggle mod channel for continuous use
/ac mod
# All future messages go to mod channel until /achat off
```

### Advanced Examples

```bash
# Scenario: Extended staff discussion in admin channel
/achat admin
Reminder: Server maintenance tonight at 10 PM
Anyone available to help with player reports?
Thanks everyone for the help today!
/achat off

# Scenario: Quick message to specific channel (no toggle)
/achat trial Welcome to the team, new moderator!
```

## Building the Plugin

### Prerequisites

- Java 21 or higher
- Gradle 8.0 or higher

### Manual Build

1. Clone the repository
2. Navigate to the project directory
3. Run the following command:

```bash
./gradlew build
```

4. The compiled JAR will be available in `build/libs/AdminChat-1.0.0.jar`

## Installation

1. Download the latest JAR file from the [Releases](../../releases) section or build it yourself
2. Place the JAR file in your server's `plugins` folder
3. Restart or reload your server
4. Configure the plugin in `plugins/AdminChat/config.yml`
5. Set up appropriate permissions for your staff roles
6. Enjoy secure staff communication!

## Customization

### Adding New Channels

You can add additional channels by extending the `channels` section in `config.yml`:

```yaml
channels:
  owner:
    permission: "adminchat.channel.owner"
    format: "&8[&4Owner&8] &4{sender}&8: &f{message}"
    prefix: "&8[&4Owner&8]"
    sound:
      enabled: true
      type: BLOCK_NOTE_BLOCK_PLING
      volume: 0.5
      pitch: 2.0
```

### Formatting Colors

Use Minecraft color codes (&) in your format strings:

- `&0` - Black
- `&1` - Dark Blue
- `&2` - Dark Green
- `&3` - Dark Aqua
- `&4` - Dark Red
- `&5` - Dark Purple
- `&6` - Gold
- `&7` - Gray
- `&8` - Dark Gray
- `&9` - Blue
- `&a` - Green
- `&b` - Aqua
- `&c` - Red
- `&d` - Light Purple
- `&e` - Yellow
- `&f` - White

### [LICENSE](LICENSE)

Licensed under the [MIT License](LICENSE).