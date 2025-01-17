module.exports = {
  darkMode: 'class',
  content: [
    "./src/**/*.{html,ts}",
  ],
  theme: {
    extend: {
      spacing: {
        26: '6.5rem',
      },
      fontSize: {
        'xsm': '0.8rem',
      },
      colors: {
        navy: '#155ba1',
      },
      fontFamily: {
        "lato": ['Lato', 'sans-serif']
      },
      borderWidth: {
        '1': '1px',
      },
      width: {
        '78%': '78%',
        '97%': '97%',
        '1/8': '12.5%',
        '7/8': '87.5%'
      }
    },
  },
  plugins: [],
}
