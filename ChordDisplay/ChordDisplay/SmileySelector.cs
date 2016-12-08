using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Media;

namespace ChordDisplay
{
    class SmileySelector
    {
        public static readonly SmileySelector Instance = new SmileySelector();

        private string[] smileyNames = new string[] {
            "iconmonstr-smiley-10-240.png",
            "iconmonstr-smiley-11-240.png",
            "iconmonstr-smiley-13-240.png",
            "iconmonstr-smiley-14-240.png",
            "iconmonstr-smiley-15-240.png",
            "iconmonstr-smiley-16-240.png",
            "iconmonstr-smiley-17-240.png",
            "iconmonstr-smiley-18-240.png",
            "iconmonstr-smiley-19-240.png",
            "iconmonstr-smiley-2-240.png",
            "iconmonstr-smiley-20-240.png",
            "iconmonstr-smiley-23-240.png",
            "iconmonstr-smiley-24-240.png",
            "iconmonstr-smiley-25-240.png",
            "iconmonstr-smiley-26-240.png",
            "iconmonstr-smiley-28-240.png",
            "iconmonstr-smiley-7-240.png",
            "iconmonstr-smiley-8-240.png",
            "iconmonstr-smiley-9-240.png" };

        private Color[] smileyColors = new[]
        {
            Colors.Blue, Colors.DodgerBlue, Colors.RoyalBlue, Colors.Aqua, Colors.CadetBlue, Colors.BlueViolet, Colors.Indigo, Colors.Fuchsia, Colors.MediumVioletRed,
            Colors.Crimson, Colors.DarkRed, Colors.DarkOrange, Colors.Gold, Colors.Chartreuse, Colors.Olive, Colors.DarkGreen
        };

        private Queue<string> freeSmilies = new Queue<string>();
        private Dictionary<string, string> idSmileyMap = new Dictionary<string, string>();

        public string GetSmiley(string id)
        {
            string smiley = null;
            if (!idSmileyMap.TryGetValue(id, out smiley))
            {
                smiley = RandomSmiley();
                idSmileyMap[id] = smiley;
            }

            return smiley;
        }

        private string RandomSmiley()
        {
            if (freeSmilies.Count == 0)
            {
                // Shuffel smiley list
                foreach (var _ in smileyNames.Shuffle()) 
                    freeSmilies.Enqueue(_);
            }

            var smiley =  freeSmilies.Dequeue();
            return SmileyUrlFromName(smiley);
        }

        private string SmileyUrlFromName(string name)
        {
            return string.Format("pack://application:,,,/Smilies/{0}", name);
        }
    }
}
