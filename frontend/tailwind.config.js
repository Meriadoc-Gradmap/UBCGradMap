/** @type {import('tailwindcss').Config} */
import tw from '@tailwindcss/typography'
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {},
  },
  plugins: [
    tw
  ],
}

