// recommendationHelper.js
import openai
import os

const openai = require('openai');
openai.api_key = os.environ["OPENAI_API_KEY"]

const helper = async (userInput) => {
    const messages = [
        {
            role: 'system',
            content:
                'You are helping the user be healthier. ' +
                'You should analyze the product that is given to you and then recommend better version products for the user. Just provide the product titles and their brand names with a maximum of four recommendations.',
        },
        {
            role: 'user',
            content: userInput,
        },
    ];

    try {
        // Call OpenAI API for chat completions
        const chat = await openai.ChatCompletion.create({
            model: 'gpt-3.5-turbo',
            messages: messages,
        });

        // Extract and return the assistant's reply
        const reply = chat.choices[0].message.content;
        return reply;
    } catch (error) {
        console.error('Error in recommending a better product:', error);
        // Handle errors gracefully
        return 'Sorry, an error occurred while processing your request. Please try again.';
    }
};

module.exports = helper;
