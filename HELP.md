# Telegram Bot API - Available Methods

In the `java-telegram-bot-api` library (for example [rubenlagus/TelegramBots](https://github.com/rubenlagus/TelegramBots)), besides
`AnswerCallbackQuery` and `SendMessage`, there are **many other methods** you can use.

Here are some commonly used ones:

| Class                    | Description                                 |
|:-------------------------|:--------------------------------------------|
| `SendMessage`            | Send a basic text message.                  |
| `SendPhoto`              | Send a photo.                               |
| `SendDocument`           | Send a document (file).                     |
| `SendVideo`              | Send a video.                               |
| `SendAudio`              | Send an audio file.                         |
| `SendVoice`              | Send a voice message (voice note).          |
| `SendLocation`           | Send a location.                            |
| `SendVenue`              | Send a venue (place).                       |
| `SendContact`            | Send a contact.                             |
| `SendSticker`            | Send a sticker.                             |
| `EditMessageText`        | Edit the text of a previously sent message. |
| `EditMessageCaption`     | Edit the caption of a media message.        |
| `EditMessageMedia`       | Edit the media content of a message.        |
| `EditMessageReplyMarkup` | Edit the inline keyboard of a message.      |
| `DeleteMessage`          | Delete a message.                           |
| `AnswerCallbackQuery`    | Answer a button press (callback query).     |
| `AnswerInlineQuery`      | Answer an inline query.                     |
| `ForwardMessage`         | Forward a message to another chat.          |
| `CopyMessage`            | Copy a message without forwarding.          |
| `GetFile`                | Retrieve file information by `file_id`.     |
| `GetUserProfilePhotos`   | Get user's profile pictures.                |
| `SetMyCommands`          | Set bot commands (for the menu).            |
| `GetMyCommands`          | Get the list of bot commands.               |
| `DeleteMyCommands`       | Delete bot commands.                        |

---

### Example: Expanding your existing code

Here’s how you could expand your original example by editing the pressed button’s message:

```java

@Override
public void execute(TelegramBot bot, CallbackQuery callback) {
    bot.execute(new AnswerCallbackQuery(callback.getId()).setText("Test"));
    bot.execute(new SendMessage(callback.getFrom().getId(), "Test"));

    bot.execute(new EditMessageText()
        .setChatId(callback.getMessage().getChatId())
        .setMessageId(callback.getMessage().getMessageId())
        .setText("The message has been edited after pressing the button"));
}
```

---

### Notes

- You can explore even more methods inside the package:  
  `org.telegram.telegrambots.meta.api.methods.*`
- All these classes usually extend `BotApiMethod` or similar helper classes.
- Typically, after a **callback button** press, developers:
    - **Answer the callback** (with `AnswerCallbackQuery`)
    - **Edit the message** (with `EditMessageText`, `EditMessageCaption`, etc.)
    - Or **Delete the message** if needed.
