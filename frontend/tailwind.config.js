module.exports = {
  content: [
    "./src/**/*.{html,ts}",
  ],
  theme: {
    extend: {
      fontFamily: {
        "lato": ['Lato', 'sans-serif']
      },
      borderWidth: {
        '1': '1px', // Define a border width of 1px
      },
      width: {
        '78%': '78%',
        '97%': '97%'
      }
    },
  },
  plugins: [],
}
