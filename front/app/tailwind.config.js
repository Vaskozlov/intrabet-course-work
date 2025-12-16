/** @type {import('tailwindcss').Config} */
module.exports = {
  darkMode: "class",
  content: [
    "./src/**/*.{js,jsx,ts,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        primary: "#FF1F1F",
        "primary-hover": "#D61616",
        "background-light": "#F3F4F6",
        "background-dark": "#09090b",
        "surface-dark": "#18181b",
        "surface-light": "#FFFFFF",
        "accent-gray": "#27272a",
        "input-bg": "#121214",
      },
      fontFamily: {
        display: ["Space Grotesk", "sans-serif"],
        body: ["Inter", "sans-serif"],
      },
      boxShadow: {
        'neon': '0 0 20px rgba(255, 31, 31, 0.4)',
        'neon-sm': '0 0 10px rgba(255, 31, 31, 0.3)',
        'glow-inset': 'inset 0 0 20px rgba(255, 31, 31, 0.1)',
      },
      keyframes: {
        'slide-in': {
          '0%': { transform: 'translateX(100%)', opacity: '0' },
          '100%': { transform: 'translateX(0)', opacity: '1' },
        },
      },
      animation: {
        'slide-in': 'slide-in 0.3s ease-out',
      },
    },
  },
  plugins: [],
}
