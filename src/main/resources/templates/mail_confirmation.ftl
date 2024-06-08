<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Email Verification - Coassemble</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            line-height: 1.6;
        }

        .container {
            max-width: 600px;
            margin: 0 auto;
            padding: 20px;
            border: 1px solid #dddddd;
            border-radius: 5px;
            background-color: #f9f9f9;
        }

        .button {
            display: inline-block;
            padding: 10px 20px;
            margin-top: 10px;
            color: #fff;
            background-color: #007BFF;
            text-decoration: none;
            border-radius: 5px;
        }
    </style>
</head>
<body>
<div class="container">
    <p>Hello ${receiverEmail},</p>
    <p>Welcome to Coassemble!</p>
    <p>To complete your registration, please verify your email address by clicking the link below:</p>
    <p><a href="${link}" class="button">Verify Email</a></p>
    <p>If the button above doesn't work, please copy and paste the following URL into your web browser:</p>
    <p><a href="${link}">${link}</a></p>
    <p>This verification link will expire in ${verificationTokenTtl} minutes.</p>
    <p>Thank you for joining us!</p>
    <p>Best regards,</p>
    <p>The Coassemble Team</p>
</div>
</body>
</html>
