using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Threading;

namespace ChordDisplay
{
    public class MainViewModel : PropertyChangedBase
    {
        FileSystemWatcher fileWatcher;

        public ObservableCollection<ChordNode> Nodes
        {
            get;
        } = new ObservableCollection<ChordNode>();


        public MainViewModel()
        {
            fileWatcher = new FileSystemWatcher(@"C:\Temp\", "*.csv");
            fileWatcher.Changed += FileWatcher_Changed;
            fileWatcher.EnableRaisingEvents = true;

            UpdateRing();
        }

        private void FileWatcher_Changed(object sender, FileSystemEventArgs e)
        {
            if (e.Name == "AllNodes.csv" && e.ChangeType == WatcherChangeTypes.Changed)
            {
                Application.Current.Dispatcher.Invoke(UpdateRing);
            }
        }

        private void UpdateRing()
        {
            List<ChordNode> newElements = ReadFile(@"C:\Temp\AllNodes.csv").ToList();

            foreach (var chordNode in Nodes.Where(chordNode => !newElements.Contains(chordNode)).ToList())
            {
                Nodes.Remove(chordNode);
            }

            foreach (var chordNode in newElements.Where(chordNode => !Nodes.Contains(chordNode)))
            {
                Nodes.Add(chordNode);
            }
        }

        private IEnumerable<ChordNode> ReadFile(string fullFilename)
        {
            using (var fileStream = new StreamReader(File.Open(fullFilename, FileMode.Open, FileAccess.Read, FileShare.Read)))
            {
                string line;
                while ((line = fileStream.ReadLine()) != null)
                {
                    var split = line.Split(';');

                    var hash = split[0];
                    var url = split[1];

                    yield return new ChordNode() {ID = hash, IP = url};
                }
            }
        }
    }
}
