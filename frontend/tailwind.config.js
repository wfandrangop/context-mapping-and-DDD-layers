/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
  theme: {
    extend: {
      colors: {
        primary: '#1A5276',
        accent: '#F39C12',
        background: '#F4F7F6',
      },
    },
  },
  plugins: [],
};
